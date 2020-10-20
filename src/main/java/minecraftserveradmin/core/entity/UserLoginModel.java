package minecraftserveradmin.core.entity;

import minecraftserveradmin.core.util.ErrorCode;

public class UserLoginModel {
    UserModel userModel;
    Integer code;
    String connect_token;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getConnect_token() {
        return connect_token;
    }

    public void setConnect_token(String connect_token) {
        this.connect_token = connect_token;
    }
}
