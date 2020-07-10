package minecraftserveradmin.core.dao;

import minecraftserveradmin.core.entity.UserModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {
    @Insert("insert into user(user_name,passwd,email,vip_level,authority,UUID) values(#{name},#{pass},#{email},#{vip_level},#{authority},#{UUID})")
    int insertUser(String name, String email, String pass, String authority, int vip_level, String UUID);
    @Select("select * from user where user_name=#{name}")
    UserModel selectUser(String name);

}