package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.UserService;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserAdministeredImpl implements UserService {
    @Autowired
    TokenUtil tokenUtil;

    UserModel userModel;
    @Autowired
    UserDao userDao;
    public int doRegister(String name, String passwd, String email, String UUID){
//        String tmp_psaa =
//                DigestUtils.md5DigestAsHex(passwd.getBytes()) +
//                        DigestUtils.md5DigestAsHex(UUID.getBytes()) +
//                        DigestUtils.md5DigestAsHex(passwd.getBytes());
//        String pass = DigestUtils.md5DigestAsHex(tmp_psaa.getBytes());
//        if(userDao.selectUser(name) == null){
//            return userDao.insertUser(name,email,pass,"def",1,UUID);
//        }
        return (0);
    }
    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response){
//        System.err.println(name);
//        System.err.println(pass);
//        System.err.println(autoLogin);
        Integer index = userDao.selectOnlineByName(name);
//        System.err.println(index);
        if (index != null){
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
                userDao.insertConnect(userModel.getUser_name(),ConnectCookie.getValue());
                userLoginModel.setUserModel(userModel);
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
    public void doLogout(String name){
        if (name!=null){
            userDao.deleteTokenByName(name);
        }
    }

    @Override
    public UserLoginModel doAutoLogin(String token) {
        return null;
    }
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
