package minecraftserveradmin.core.services;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserService {
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
            return userDao.insertUser(name,email,pass,"def",1,UUID);
        }
        return (0);

    };
    public void doLogin(){

    };
    public void doLogout(){

    };
}
