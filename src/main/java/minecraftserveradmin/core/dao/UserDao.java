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

    @Select("select * from user where user_name=#{name}")
    UserModel selectUser(String name);
    @Select("select id from user where user_name=#{name} and authority='admin'")
    Integer selectAdminUser(String name);
    @Select("select id from user where authority='admin'")
    Integer[] selectAdminUserWithOutName();
//    @Select("select name from autologin where token=#{token}")
//    String selectAutoByToken(String token);
//    @Select("select id from autologin where name=#{name}")
//    Integer selectOnlineByName(String name);



}