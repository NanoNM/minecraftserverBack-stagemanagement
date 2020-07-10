package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.UserLoginModel;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface UserService {
    int doRegister(String name, String passwd, String email, String UUID);
    UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response);
    void doLogout();
}
