package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.OlineUserModel;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.UserService;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.LogUtil;
import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class UserAdministeredImpl implements UserService {
    /**
     * 废弃数据库存储在线管理员 使用Map存储管理员
     */
    public static final List<OlineUserModel> onlineadmin = new CopyOnWriteArrayList<>();

    @Autowired
    TokenUtil tokenUtil;

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

    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response){
//        Integer index = userDao.selectOnlineByName(name);

        boolean flag = testOnline(name);
        if (flag){
            UserLoginModel userLoginModel = new UserLoginModel();
            userLoginModel.setCode(ErrorCode.ADMIN_EARLY_ONLINE);
            return userLoginModel;
        }
        userModel = userDao.selectUser(name);
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
            if (!"admin".equals(isAdmin)){
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.USER_NOT_ADMIN);
                return userLoginModel;
            }
            if (passwd.equals(userModel.getPasswd()) && autoLogin.equals("true")){
                Cookie AutoCookie = tokenUtil.getAutoLoginToken();
                response.addCookie(AutoCookie);
                Cookie ConnectCookie = tokenUtil.getConnect();
                response.addCookie(ConnectCookie);
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
                userModel.setUUID(null);
                userModel.setPasswd(null);
                userLoginModel.setUserModel(userModel);
                userDao.insertAutoLogin(userModel.getUser_name(),AutoCookie.getValue(),ConnectCookie.getValue());
                return userLoginModel;
            }else if(passwd.equals(userModel.getPasswd()) && autoLogin.equals("false")){
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
                userModel.setUUID(null);
                userModel.setPasswd(null);
                Cookie ConnectCookie = tokenUtil.getAutoLoginToken();
                response.addCookie(ConnectCookie);
//                userDao.insertConnect(userModel.getUser_name(),ConnectCookie.getValue());
                userLoginModel.setUserModel(userModel);
                onlineadmin.add(new OlineUserModel(name, null));
                return userLoginModel;
            }else{
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.WRONG_PASSWORD);
                return userLoginModel;
            }
        }
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
    public void doLogout(String name){
        if (name!=null){
            userDao.deleteTokenByName(name);
        }
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

    /**
     * 项目初始化方法
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
