package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping("/register")
    public String userRegister(@Param("name") String name,
                               @Param("passwd") String passwd,
                               @Param("email") String email,
                               @Param("UUID") String UUID
                               ){
        if (userService.doRegister(name,passwd,email,UUID) == 1){
            return "注册成功";
        }
        return "注册失败";
    }
}
