package minecraftserveradmin.core.services;

import minecraftserveradmin.core.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


/**
 *解决service中@Autowired注解注入Dao层时出现null的问题 解决方法来自
 * https://blog.csdn.net/vleess/article/details/82429416?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.control
 */
@Service
public class SocketHandlerServices {

    @Autowired
    public UserDao UserDao;

    //新增静态变量
    public static SocketHandlerServices proxy;

    @PostConstruct
    public void init() {
        proxy = this;
    }
}
