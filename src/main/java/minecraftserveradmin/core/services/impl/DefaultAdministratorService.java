package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.services.OnStartService;
import minecraftserveradmin.core.util.LogUtil;
import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static minecraftserveradmin.core.util.RandomPasswd.randomPassword;

@Component
@Order(value = 1)
public class DefaultAdministratorService implements OnStartService {
    @Autowired
    UserDao userDao;
    @Value("${MCM.Default.administrator.username}")
    String defaultAdministrator;
    @Value("${MCM.Default.administrator.password}")
    String defaultAdministratorPassword;
    @Override
    public void run(ApplicationArguments args) {
//        userDao.selectUserCreateBySystem();
//        if (==0){
//
//        }
        if (userDao.selectAdminUserWithOutName().length==0){
            if ("admin".equals(defaultAdministratorPassword)){
                defaultAdministratorPassword = randomPassword();
            }
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replace("-", "");
            String pass = TokenUtil.getPassword(defaultAdministratorPassword,uuid);
            userDao.insertAdminUser(defaultAdministrator,"",pass,"admin",-1,uuid,"system");
            LogUtil.log.info("默认管理员已添加");
            LogUtil.log.info("默认管理员密码: " + defaultAdministratorPassword);
        }
    }
}
