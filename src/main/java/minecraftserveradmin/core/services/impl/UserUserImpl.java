package minecraftserveradmin.core.services.impl;

//import minecraftserveradmin.core.dao.AuthmeDao;
import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.OlineUserModel;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.UserService;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.LoginAttemptLimiter;
import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

import static minecraftserveradmin.core.services.impl.UserAdministeredImpl.getUserLoginModel;

@Service
public class UserUserImpl implements UserService {

    @Value("${MCM.Minecraft.BindingForumWithLoginPlugin}")
    Boolean bindingForumWithLoginPlugin;

    @Value("${MCM.Minecraft.logplugin.name}")
    String logpluginName;

    @Value("${MCM.Minecraft.logplugin.table}")
    String logpluginTable;

    @Autowired 
    UserAdministeredImpl userAdministeredImpl;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    LoginAttemptLimiter loginAttemptLimiter;

    UserModel userModel;
    @Autowired
    UserDao userDao;

//    @Autowired
//    AuthmeDao authmeDao;


//        String tmp_psaa =
//                DigestUtils.md5DigestAsHex(passwd.getBytes()) +
//                        DigestUtils.md5DigestAsHex(UUID.getBytes()) +
//                        DigestUtils.md5DigestAsHex(passwd.getBytes());
//        String pass = DigestUtils.md5DigestAsHex(tmp_psaa.getBytes());
//        if(userDao.selectUser(name) == null){
//            if (userDao.insertUser(name,email,pass,"def",1,UUID) == 1)
//                return ErrorCode.REGISTER_SUCCESS;
//        }
//        return (ErrorCode.SAME_USER_NAME);
//    }

    @Override
    public Integer doRegister(String regName, String name, String passwd, String email) {
        Integer flag = userDao.selectAdminUser(regName);
        if(flag==null){
            return ErrorCode.ADMIN_ALREADY_FAIL;
        }
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        String tmp_pass =
                        DigestUtils.md5DigestAsHex(passwd.getBytes()) +
                        DigestUtils.md5DigestAsHex(uuid.getBytes()) +
                        DigestUtils.md5DigestAsHex(passwd.getBytes());
        String pass = DigestUtils.md5DigestAsHex(tmp_pass.getBytes());
        if(userDao.selectUser(name) == null){
            if (userDao.insertAdminUser(name,email,pass,"user",1,uuid,regName) == 1){
                return ErrorCode.USER_REG_SUCCESS;
            }
        }else{
            return ErrorCode.ADMIN_ALREADY_EXISTS;
        }
        return ErrorCode.ADMIN_ALREADY_FAIL;
    }
    @Override
    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response){
        // 该用户名连续失败次数过多，处于锁定期，直接拒绝
        if (loginAttemptLimiter.isLocked(name)){
            UserLoginModel userLoginModel = new UserLoginModel();
            userLoginModel.setCode(ErrorCode.LOGIN_LOCKED);
            return userLoginModel;
        }
        userModel = userDao.selectUser(name);
        if(userModel == null){
            UserLoginModel userLoginModel = new UserLoginModel();
            userLoginModel.setCode(ErrorCode.NO_USER_FIND);
            return userLoginModel;
        }else{
            // 管理员不允许通过用户接口登陆
            if ("admin".equals(userModel.getAuthority())){
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.NO_USER_FIND);
                return userLoginModel;
            }
            String tmp_pass = DigestUtils.md5DigestAsHex(pass.getBytes()) +
                    DigestUtils.md5DigestAsHex(userModel.getUUID().getBytes()) +
                    DigestUtils.md5DigestAsHex(pass.getBytes());
            String passwd = DigestUtils.md5DigestAsHex(tmp_pass.getBytes());
            if (passwd.equals(userModel.getPasswd()) && "true".equals(autoLogin)){
                loginAttemptLimiter.reset(name);
                return getUserLoginModel(response, tokenUtil, userModel, userDao);
            }else if(passwd.equals(userModel.getPasswd()) && "false".equals(autoLogin)){
                loginAttemptLimiter.reset(name);
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
                userModel.setUUID(null);
                userModel.setPasswd(null);
                Cookie ConnectCookie = tokenUtil.getAutoLoginToken();
                response.addCookie(ConnectCookie);
                // 清理该用户此前的登录记录，保证同一账号只保留最新一次登录
                userDao.deleteTokenByName(userModel.getUser_name());
                userDao.insertConnect(userModel.getUser_name(),ConnectCookie.getValue());
                userLoginModel.setUserModel(userModel);
                return userLoginModel;
            }else{
                loginAttemptLimiter.recordFailure(name);
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.WRONG_PASSWORD);
                return userLoginModel;
            }
        }
    }
    @Override
    public String doLogout(String name){
        if (name!=null){
            userDao.deleteTokenByName(name);
            for (OlineUserModel olineUserModel:userAdministeredImpl.onlineadmin){
                if (olineUserModel.getUserID().equals(name)){
                    userAdministeredImpl.onlineadmin.remove(olineUserModel);
                    return "success";
                }
            }
        }
        return "failure";
    }

    /**
     * 根据登录时下发的 cookie 反查登录用户名，用于登出时确认身份
     *
     * @param token 客户端携带的 token 或 connect_token cookie 值
     * @return 对应的用户名，查不到时返回 null
     */
    public String getNameByToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return userDao.selectNameByToken(token);
    }

    @Override
    public UserLoginModel doAutoLogin(String token) {
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
        return null;
    }

    public Object[] selectAllUser(Integer page) {
        int size = 10;
        Integer cu = (page-1)*size;
//        if (bindingForumWithLoginPlugin){
//            return authmeDao.selectAllUserFromAuthme(cu,size,logpluginTable);
//        }else{
//
//
//        }
        return userDao.selectAllUser(cu,size);
    }

    public UserModel[] selectUser(String name) {
        return userDao.selectUserByName(name);
    }
}
