package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.UserService;
import minecraftserveradmin.core.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

@Service
public class UserUserImpl implements UserService {

    UserModel userModel;
    @Autowired
    UserDao userDao;
    public int doRegister(String name, String passwd, String email, String UUID){
        String tmp_psaa =
                DigestUtils.md5DigestAsHex(passwd.getBytes()) +
                        DigestUtils.md5DigestAsHex(UUID.getBytes()) +
                        DigestUtils.md5DigestAsHex(passwd.getBytes());
        String pass = DigestUtils.md5DigestAsHex(tmp_psaa.getBytes());
        if(userDao.selectUser(name) == null){
            if (userDao.insertUser(name,email,pass,"def",1,UUID) == 1)
                return ErrorCode.REGISTER_SUCCESS;
        }
        return (ErrorCode.SAME_USER_NAME);
    };
    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response){
//        System.err.println(name);
//        System.err.println(pass);
//        System.err.println(autoLogin);
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
                Random random = new Random(1000);
                long tmp_token = random.nextInt(1000)+System.currentTimeMillis()+random.nextInt(1000);
                String token = DigestUtils.md5DigestAsHex(String.valueOf(tmp_token).getBytes());

                response.addCookie(new Cookie("token",token));
                UserLoginModel userLoginModel = new UserLoginModel();
                //response.();
                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
                userLoginModel.setUserModel(userModel);
                userDao.insertAutoLogin(userModel.getUser_name(),token);
                return userLoginModel;
            }else if(passwd.equals(userModel.getPasswd()) && autoLogin.equals("false")){
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.LOGIN_SUCCESS);
                userLoginModel.setUserModel(userModel);
                return userLoginModel;
            }else{
                UserLoginModel userLoginModel = new UserLoginModel();
                userLoginModel.setCode(ErrorCode.WRONG_PASSWORD);
                return userLoginModel;
            }
        }
    }
    public void doLogout(){

    }


}
