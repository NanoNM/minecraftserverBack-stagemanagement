package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class UserAdministeredImpl implements UserService {
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

    @Override
    public UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response) {
        return null;
    }

    @Override
    public void doLogout(){

    };
}
