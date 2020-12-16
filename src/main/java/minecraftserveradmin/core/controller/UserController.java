package minecraftserveradmin.core.controller;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.services.impl.UserAdministeredImpl;
import minecraftserveradmin.core.services.impl.UserUserImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {
    @Autowired
    UserUserImpl userUserImpl;
    @Autowired
    UserAdministeredImpl userAdministeredImpl;
    @PostMapping("/register")
    public String userRegister(@RequestParam("name") String name,
                               @RequestParam("passwd") String passwd,
                               @RequestParam("email") String email,
                               @RequestParam("UUID") String UUID
                               ){
        if (userUserImpl.doRegister(name,passwd,email,UUID) == 1){
            return "注册成功";
        }
        return "注册失败";
    }
    @PostMapping("/login")
    public UserLoginModel userLogin(@Param("username") String name,
                            @Param("passwd") String passwd,
                            @Param("isautologin") String isautologin,
                            HttpServletResponse response

    ){
        return userUserImpl.doLogin(name,passwd,isautologin,response);
    }
//    @PostMapping("/admin/login")
//    public UserLoginModel adminLogin(@RequestParam("name") String name,
//                                     @RequestParam("passwd") String passwd,
//                                     @RequestParam("isautologin") String isautologin,
//                                    HttpServletResponse response
//
//    ){
//        return userAdministeredImpl.doLogin(name,passwd,"false",response);
//    }


    @PostMapping("/admin/login")
    public UserLoginModel adminLogin(@RequestBody JSONObject jsonObject,
                                     HttpServletResponse response){
        return userAdministeredImpl.doLogin(jsonObject.getString("username"),jsonObject.getString("password"),"false",response);
    }
    @GetMapping("/autologin")
    private UserLoginModel autoLogin(@Param("token") String token){
        return userUserImpl.doAutoLogin(token);
    }
    @GetMapping("/userloginout")
    private void userLoginOut(@Param("name") String name){
        userUserImpl.doLogout(name);
    }
}
