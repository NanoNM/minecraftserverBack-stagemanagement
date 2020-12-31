package minecraftserveradmin.core.controller;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
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

    @GetMapping("/admin/getalladmin")
    private UserModel[] selectAllAdmin(@RequestParam("page") Integer page){
        return userAdministeredImpl.selectAllAdmin(page);
    }

    @PostMapping("/admin/modifypassword")
    private Integer modifyPassword(@RequestParam("newps") String passwd, @RequestParam("username") String username){
        return userAdministeredImpl.modifyPassword(passwd,username);
    }

    @GetMapping("/admin/deleteAdministrator")
    private Integer deleteAdmin(@RequestParam("name") String name, @RequestParam("username") String username){
        return userAdministeredImpl.deleteAdmin(name, username);
    }

    @PostMapping("/admin/register")
    public Integer userRegister(@RequestParam("name") String adminName,
                                @RequestParam("regname") String name,
                                @RequestParam("passwd") String passwd,
                                @RequestParam("email") String email
    ) {
        return userAdministeredImpl.doAdminRegister(adminName, name, passwd, email);
    }

    @PostMapping("/login")
    public UserLoginModel userLogin(@RequestParam("username") String name,
                                    @RequestParam("passwd") String passwd,
                                    @RequestParam("isautologin") String isautologin,
                                    HttpServletResponse response

    ) {
        return userUserImpl.doLogin(name, passwd, isautologin, response);
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
                                     HttpServletResponse response) {
        return userAdministeredImpl.doLogin(jsonObject.getString("username"), jsonObject.getString("password"), "false", response);
    }

    @GetMapping("/autologin")
    private UserLoginModel autoLogin(@RequestParam("token") String token) {
        return userUserImpl.doAutoLogin(token);
    }

    @GetMapping("/userloginout")
    private void userLoginOut(@RequestParam("name") String name) {
        userUserImpl.doLogout(name);
    }


}
