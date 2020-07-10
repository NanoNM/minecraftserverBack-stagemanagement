package minecraftserveradmin.core.entity;

import minecraftserveradmin.core.util.ErrorCode;

public class UserLoginModel {
    UserModel userModel;
    Integer code;

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
}
