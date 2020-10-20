package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.services.OnStartService;
import minecraftserveradmin.core.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class DelectAllOnlineUserlmpl implements OnStartService {
    @Autowired
    UserDao userDao;
    @Override
    public void run(ApplicationArguments args) {
        LogUtil.log.info("delete all online user from databases !!");
        userDao.deleteAllOnLine();
    }
}
