package minecraftserveradmin.core.controller;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.dto.ResultBody;
import minecraftserveradmin.core.entity.UserLoginModel;
import minecraftserveradmin.core.entity.UserModel;
import minecraftserveradmin.core.services.impl.UserAdministeredImpl;
import minecraftserveradmin.core.services.impl.UserUserImpl;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.LogUtil;
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
    private ResultBody<UserModel[]> selectAllAdmin(@RequestParam("page") Integer page){
        return ResultBody.success(userAdministeredImpl.selectAllAdmin(page));
    }

    @GetMapping("/admin/getalluser")
    private ResultBody<Object[]> selectAllUser(@RequestParam("page") Integer page){
        return ResultBody.success(userUserImpl.selectAllUser(page));
    }

    @GetMapping("/admin/suchuserbyname")
    private ResultBody<UserModel[]> selectUserByName(@RequestParam("suchname") String name){
        return ResultBody.success(userUserImpl.selectUser(name));
    }

    @PostMapping("/admin/modifypassword")
    private ResultBody<Integer> modifyPassword(@RequestParam("newps") String passwd, @RequestParam("username") String username){
        return wrap(userAdministeredImpl.modifyPassword(passwd,username));
    }

    @GetMapping("/admin/deleteAdministrator")
    private ResultBody<Integer> deleteAdmin(@RequestParam("name") String name, @RequestParam("username") String username){
        return wrap(userAdministeredImpl.deleteAdmin(name, username));
    }

    @PostMapping("/admin/register")
    public ResultBody<Integer> adminUserRegister(@RequestParam("name") String adminName,
                                                 @RequestParam("regname") String name,
                                                 @RequestParam("passwd") String passwd,
                                                 @RequestParam("email") String email
    ) {
        return wrap(userAdministeredImpl.doRegister(adminName, name, passwd, email));
    }

    @PostMapping("/admin/registeruser")
    public ResultBody<Integer> userRegister(@RequestParam("name") String adminName,
                                            @RequestParam("regname") String name,
                                            @RequestParam("passwd") String passwd,
                                            @RequestParam("email") String email
    ) {
        return wrap(userUserImpl.doRegister(adminName, name, passwd, email));
    }

    @PostMapping("/login")
    public ResultBody<UserLoginModel> userLogin(@RequestParam("username") String name,
                                                @RequestParam("passwd") String passwd,
                                                @RequestParam("isautologin") String isautologin,
                                                HttpServletResponse response

    ) {
        return ResultBody.success(userUserImpl.doLogin(name, passwd, isautologin, response));
    }

    @PostMapping("/admin/login")
    public ResultBody<UserLoginModel> adminLogin(@RequestBody JSONObject jsonObject,
                                                 HttpServletResponse response) {
        LogUtil.log.info(jsonObject.getString("username"));
        return ResultBody.success(userAdministeredImpl.doLogin(jsonObject.getString("username"), jsonObject.getString("password"), "false", response));
    }

    @GetMapping("/autologin")
    private ResultBody<UserLoginModel> autoLogin(@RequestParam("token") String token) {
        return ResultBody.success(userUserImpl.doAutoLogin(token));
    }

    @PostMapping("/userloginout")
    private ResultBody<String> userLoginOut(@CookieValue(value = "token", required = false) String token,
                                            @CookieValue(value = "connect_token", required = false) String connectToken) {
        // 身份由 cookie 反查得出，不信任客户端传入的用户名
        String name = userUserImpl.getNameByToken(token);
        if (name == null) {
            name = userUserImpl.getNameByToken(connectToken);
        }
        if (name == null) {
            return ResultBody.error(ErrorCode.USER_NOT_ONLINE, "用户未登录");
        }
        return ResultBody.success(userUserImpl.doLogout(name));
    }

    /**
     * 服务层以 ErrorCode 表达结果：10xx 为成功，3xxx 为失败，0 表示无权限。
     */
    private ResultBody<Integer> wrap(Integer code) {
        if (code != null && code >= 1000 && code < 3000) {
            return ResultBody.success(code, code);
        }
        if (code != null && code == 0) {
            return ResultBody.error(ErrorCode.USER_NOT_ADMIN, "无权限执行该操作");
        }
        return ResultBody.error(code, "操作失败");
    }
}
