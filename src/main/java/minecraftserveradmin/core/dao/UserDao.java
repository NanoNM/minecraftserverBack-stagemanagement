package minecraftserveradmin.core.dao;

import minecraftserveradmin.core.entity.UserModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface UserDao {
    @Insert("insert into user(user_name,passwd,email,vip_level,authority,UUID,create_by) values(#{name},#{pass},#{email},#{vip_level},#{authority},#{UUID},#{createBy})")
    int insertAdminUser(String name, String email, String pass, String authority, int vip_level, String UUID, String createBy);
    @Insert("insert into autologin(name,token,connect_token) values(#{name},#{token},#{connectToken})")
    int insertAutoLogin(String name, String token, String connectToken);
    @Insert("insert into autologin(name,token,connect_token) values(#{name},'null',#{token})")
    int insertConnect(String name, String token);

    @Delete("delete from autologin where name=#{name}")
    Integer deleteTokenByName(String name);
    @Delete("delete from autologin")
    Integer deleteAllOnLine();
    @Delete("delete from user where user_name=#{name}")
    Integer deleteAdminUser(String name);

    @Select("select * from user where user_name=#{name}")
    UserModel selectUser(String name);
    @Select("select create_time,modify_time from user where create_by='system'")
    UserModel selectUserCreateBySystem();
    @Select("select id from user where user_name=#{name} and authority='admin'")
    Integer selectAdminUser(String name);
    @Select("select id from user where user_name=#{name} and authority!='admin'")
    Integer selectUserUser(String name);
    @Select("select id from user where authority='admin'")
    Integer[] selectAdminUserWithOutName();
    @Select("select id,user_name,create_time,modify_time,create_by from user where authority='admin' limit #{page},#{size}")
    UserModel[] selectAllAdmin(Integer page,Integer size);
    @Select("select id,authority,user_name,create_time,modify_time,create_by from user where authority!='admin' limit #{page},#{size}")
    UserModel[] selectAllUser(Integer page, int size);
    @Select("select id,authority,user_name,create_time,modify_time,create_by from user where authority!='admin' and user_name=#{name}")
    UserModel[] selectUserByName(String name);

    @Update("UPDATE user SET passwd=#{passwd},modify_time=CURRENT_TIMESTAMP,UUID=#{uuid} WHERE user_name=#{username}")
    int adminUserChangePassword(String passwd, String username,String uuid);



//    @Select("select name from autologin where token=#{token}")
//    String selectAutoByToken(String token);
//    @Select("select id from autologin where name=#{name}")
//    Integer selectOnlineByName(String name);
}