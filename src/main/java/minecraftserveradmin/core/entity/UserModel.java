package minecraftserveradmin.core.entity;

import java.sql.Timestamp;

public class UserModel {
    private Integer ID;
    private String user_name;
    private String passwd;
    private String email;
    private Integer vip_level;
    private String authority;
    private String UUID;
    private Timestamp create_time;
    private Timestamp modify_time;
    private Timestamp last_login_time;
    private String create_by;
    private String realname;

    public String getAuthme_id() {
        return realname;
    }

    public void setAuthme_id(String realname) {
        this.realname = realname;
    }

    public Timestamp getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Timestamp last_login_time) {
        this.last_login_time = last_login_time;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getVip_level() {
        return vip_level;
    }

    public void setVip_level(Integer vip_level) {
        this.vip_level = vip_level;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getModify_time() {
        return modify_time;
    }

    public void setModify_time(Timestamp modify_time) {
        this.modify_time = modify_time;
    }
}

