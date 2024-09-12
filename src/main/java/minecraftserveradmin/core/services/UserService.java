package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.UserLoginModel;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public interface UserService {
    Integer doRegister(String adminName, String name, String passwd, String email);
    UserLoginModel doLogin(String name, String pass, String autoLogin, HttpServletResponse response);
    void doLogout(String name);
    UserLoginModel doAutoLogin(String token);
}
