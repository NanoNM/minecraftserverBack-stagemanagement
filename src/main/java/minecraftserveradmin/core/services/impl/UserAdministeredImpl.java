package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.controller.WebSocketService;
import minecraftserveradmin.core.entity.OlineUserModel;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.UserService;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.LogUtil;
import minecraftserveradmin.core.util.LoginAttemptLimiter;
import minecraftserveradmin.core.util.TokenUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
// @Deprecated
public class UserAdministeredImpl implements UserService {
    /**
     * 废弃数据库存储在线管理员 使用Map存储管理员
     */
    public static final List<OlineUserModel> onlineadmin = new CopyOnWriteArrayList<>();

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    LoginAttemptLimiter loginAttemptLimiter;

    UserModel userModel;
    @Autowired
    UserDao userDao;
    @Override
    public Integer doRegister(String adminName, String name, String passwd, String email){
        Integer flag = userDao.selectAdminUser(adminName);
        if(flag==null){
            return (0);
        }
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        String tmp_pass =
                        DigestUtils.md5DigestAsHex(passwd.getBytes()) +
                        DigestUtils.md5DigestAsHex(uuid.getBytes()) +
                        DigestUtils.md5DigestAsHex(passwd.getBytes());
        String pass = DigestUtils.md5DigestAsHex(tmp_pass.getBytes());
        if(userDao.selectUser(name) == null){
            if (userDao.insertAdminUser(name,email,pass,"admin",-1,uuid,adminName) == 1){
                return ErrorCode.ADMIN_REG_SUCCESS;
            }
        }else{
            return ErrorCode.ADMIN_ALREADY_EXISTS;
        }
        return ErrorCode.ADMIN_ALREADY_FAIL;
    }

    boolean testOnline(String name){
        for (OlineUserModel s : UserAdministeredImpl.onlineadmin) {
            if (s.getUserID().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 踢掉该用户名的旧登录会话。
     * <p>
     * 新登录顶替旧登录时调用：关闭旧 WebSocket 会话，并从在线会话、token 列表、
     * 在线管理员列表中移除对应条目，保证同一账号只保留最新一次登录。
     *
     * @param name 用户名
     */
    public static void kickOldSession(String name){
        if (name == null){
            return;
        }
        for (OlineUserModel model : onlineadmin){
            if (!name.equals(model.getUserID())){
                continue;
            }
            Session oldSession = model.getSession();
            if (oldSession != null){
                String oldSessionId = oldSession.getId();
                LogUtil.log.warn("账号 " + name + " 在新位置登录，踢掉旧会话: " + oldSessionId);
                WebSocketService.onlineSessions.remove(oldSessionId);
                WebSocketService.AOPtokens.removeIf(
                        token -> token.getSession() != null
                                && oldSessionId.equals(token.getSession().getId()));
                try {
                    oldSession.close();
                } catch (IOException e){
                    LogUtil.log.error("关闭旧会话失败: " + e.getMessage());
                }
            }
            onlineadmin.remove(model);
        }
    }

    /**
     * 管理员登录
     * <p>
     * 校验顺序：是否已在线 -> 用户是否存在 -> 是否为管理员 -> 密码是否正确。
     * 密码校验方式为 MD5(MD5(明文) + MD5(UUID) + MD5(明文))，与注册时写入的密文比对。
     * 登录成功后把该管理员加入在线列表 onlineadmin，并更新最后登录时间。
     *
     * @param name       管理员用户名
     * @param pass       明文密码
     * @param autoLogin  "true" 走自动登录（写入 autologin 表并下发 cookie），"false" 仅下发 cookie
     * @param response   用于写入登录 cookie
     * @return 登录结果，code 为 ErrorCode 中的状态码；成功时携带用户信息，失败时仅携带 connect_token
     */
    @Override
    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response){
//        Integer index = userDao.selectOnlineByName(name);
        TokenUtil tokenUtil = new TokenUtil();
        String token = tokenUtil.getRandomString();
        // 该用户名连续失败次数过多，处于锁定期，直接拒绝
        if (loginAttemptLimiter.isLocked(name)){
            UserLoginModel userLoginModel = new UserLoginModel();
            userLoginModel.setCode(ErrorCode.LOGIN_LOCKED);
            userLoginModel.setConnect_token(token);
            LogUtil.log.warn("账号 " + name + " 登录失败次数过多，已锁定 "
                    + loginAttemptLimiter.getRemainingLockSeconds(name) + " 秒");
            return userLoginModel;
        }
        boolean flag = testOnline(name);
        // 该管理员已在线时踢掉旧会话，允许新登录顶替
        if (flag){
            kickOldSession(name);
        }
        userModel = userDao.selectUser(name);
        // 数据库中查不到该用户名
        if(userModel == null){
            UserLoginModel userLoginModel = new UserLoginModel();
            userLoginModel.setCode(ErrorCode.NO_USER_FIND);
            return userLoginModel;
        }else{
            String tmp_pass = DigestUtils.md5DigestAsHex(pass.getBytes()) +
                    DigestUtils.md5DigestAsHex(userModel.getUUID().getBytes()) +
                    DigestUtils.md5DigestAsHex(pass.getBytes());
            String passwd = DigestUtils.md5DigestAsHex(tmp_pass.getBytes());
            String isAdmin = userModel.getAuthority();
            // 账号存在但不是管理员，拒绝登录管理端
            if (!"admin".equals(isAdmin)){
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.USER_NOT_ADMIN);
                return userLoginModel;
            }
            // 密码正确且要求自动登录：写入 autologin 表并下发自动登录 cookie
            if (passwd.equals(userModel.getPasswd()) && "true".equals(autoLogin)){
                loginAttemptLimiter.reset(name);
                return getUserLoginModel(response, tokenUtil, userModel, userDao);
            // 密码正确但不要求自动登录：仅下发 cookie，并把管理员加入在线列表
            }else if(passwd.equals(userModel.getPasswd()) && "false".equals(autoLogin)){
                loginAttemptLimiter.reset(name);
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
                userLoginModel.setConnect_token(token);
                userModel.setUUID(null);
                userModel.setPasswd(null);
                Cookie ConnectCookie = tokenUtil.getAutoLoginToken();
                response.addCookie(ConnectCookie);
                // 清理该用户此前的登录记录，保证同一账号只保留最新一次登录
                userDao.deleteTokenByName(userModel.getUser_name());
                userDao.insertConnect(userModel.getUser_name(),ConnectCookie.getValue());
                userLoginModel.setUserModel(userModel);
                onlineadmin.add(new OlineUserModel(name, null));
                userDao.changeAdminLastLogin(userModel.getUser_name());
                return userLoginModel;
            // 密码不匹配
            }else{
                loginAttemptLimiter.recordFailure(name);
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.WRONG_PASSWORD);
                userLoginModel.setConnect_token(token);
                return userLoginModel;
            }
        }
    }

    @NotNull
    static UserLoginModel getUserLoginModel(HttpServletResponse response, TokenUtil tokenUtil, UserModel userModel, UserDao userDao) {
        Cookie AutoCookie = tokenUtil.getAutoLoginToken();
        response.addCookie(AutoCookie);
        Cookie ConnectCookie = tokenUtil.getConnect();
        response.addCookie(ConnectCookie);
        UserLoginModel userLoginModel = new UserLoginModel();
        userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
        userModel.setUUID(null);
        userModel.setPasswd(null);
        userLoginModel.setUserModel(userModel);
        // 清理该用户此前的登录记录，保证同一账号只保留最新一次登录
        userDao.deleteTokenByName(userModel.getUser_name());
        userDao.insertAutoLogin(userModel.getUser_name(),AutoCookie.getValue(),ConnectCookie.getValue());
        return userLoginModel;
    }

    //    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response){
//        System.err.println(name);
////        System.err.println(pass);
////        System.err.println(autoLogin);
//
//        if(userModel == null){
//            UserLoginModel userLoginModel = new UserLoginModel();
//            userLoginModel.setCode(ErrorCode.NO_USER_FIND);
//            return userLoginModel;
//        }else{
//            String tmp_pass = DigestUtils.md5DigestAsHex(pass.getBytes()) +
//                    DigestUtils.md5DigestAsHex(userModel.getUUID().getBytes()) +
//                    DigestUtils.md5DigestAsHex(pass.getBytes());
//            String passwd = DigestUtils.md5DigestAsHex(tmp_pass.getBytes());
//            if (passwd.equals(userModel.getPasswd()) && autoLogin.equals("true")){
//                Cookie AutoCookie = tokenUtil.getAutoLoginToken();
//                response.addCookie(AutoCookie);
//                Cookie ConnectCookie = tokenUtil.getConnect();
//                response.addCookie(ConnectCookie);
//                UserLoginModel userLoginModel = new UserLoginModel();
//                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
//                userModel.setUUID(null);
//                userModel.setPasswd(null);
//                userLoginModel.setUserModel(userModel);
//                userDao.insertAutoLogin(userModel.getUser_name(),AutoCookie.getValue(),ConnectCookie.getValue());
//                return userLoginModel;
//            }else if(passwd.equals(userModel.getPasswd()) && autoLogin.equals("false")){
//                UserLoginModel userLoginModel = new UserLoginModel();
//                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
//                userModel.setUUID(null);
//                userModel.setPasswd(null);
//                Cookie ConnectCookie = tokenUtil.getAutoLoginToken();
//                response.addCookie(ConnectCookie);
//                userDao.insertConnect(userModel.getUser_name(),ConnectCookie.getValue());
//                userLoginModel.setUserModel(userModel);
//                return userLoginModel;
//            }else{
//                UserLoginModel userLoginModel = new UserLoginModel();
//                userLoginModel.setCode(ErrorCode.WRONG_PASSWORD);
//                return userLoginModel;
//            }
//        }
//    }
    @Override
    public String doLogout(String name){
        if (name!=null){
            userDao.deleteTokenByName(name);
        }
        return "failure";
    }

    @Override
    public UserLoginModel doAutoLogin(String token) {
        return null;
    }

    public UserModel[] selectAllAdmin(Integer page) {
        int size = 10;
        Integer cu = (page-1)*size;
        return userDao.selectAllAdmin(cu,size);
    }

    public Integer modifyPassword(String passwd, String username) {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        String fanlpasswd = TokenUtil.getPassword(passwd,uuid);
        if (userDao.adminUserChangePassword(fanlpasswd,username,uuid)>0){
            return ErrorCode.USER_CHANGE_PASSWORD_SUCCESS;
        }
        return ErrorCode.USER_CHANGE_PASSWORD_FAIL;
    }

    public Integer deleteAdmin(String name, String username) {
        Integer flag = userDao.deleteAdminUser(username);
        if (flag>0){
            LogUtil.log.info(name + "执行了删除成员操作! 删除了" + flag + "条数据");
            return ErrorCode.DELETE_ADMINUSER_SUCCESS;
        }
        return ErrorCode.DELETE_ADMINUSER_FAIL;
    }

    /*
      项目初始化方法
     */
//    @PostConstruct
//    void serverInit(){
//        Integer admins = userDao.selectAdminUser();
//        System.out.println(admins);
//    }
    //为了安全 管理员并没有自动登陆选项
//    public UserLoginModel doAutoLogin(String token) {
//        if(!"null".equals(token)){
//            String name = userDao.selectAutoByToken(token);
//            if (name!=null){
//                userModel = userDao.selectUser(name);
//                UserLoginModel userLoginModel = new UserLoginModel();
//                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
//                userModel.setUUID(null);
//                userModel.setPasswd(null);
//                userLoginModel.setUserModel(userModel);
//                return userLoginModel;
//            }
//        }
//        return null;
//    }
}
