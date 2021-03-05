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
import java.util.UUID;

import static minecraftserveradmin.core.services.impl.UserAdministeredImpl.getUserLoginModel;

@Service
public class UserUserImpl implements UserService {
    @Autowired
    TokenUtil tokenUtil;

    UserModel userModel;
    @Autowired
    UserDao userDao;
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

    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response){
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
                return getUserLoginModel(response, tokenUtil, userModel, userDao);
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
    @Override
    public void doLogout(String name){
        if (name!=null){
            userDao.deleteTokenByName(name);
        }
    }

    @Override
    public UserLoginModel doAutoLogin(String token) {
        if(!"null".equals(token)){
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
        }
        return null;
    }

    public UserModel[] selectAllUser(Integer page) {
        int size = 10;
        Integer cu = (page-1)*size;
        return userDao.selectAllUser(cu,size);
    }

    public UserModel[] selectUser(String name) {
        return userDao.selectUserByName(name);
    }
}
