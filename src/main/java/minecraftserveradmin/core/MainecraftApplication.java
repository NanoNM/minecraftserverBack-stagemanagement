package minecraftserveradmin.core;

import minecraftserveradmin.core.util.LogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class MainecraftApplication {

    public static void main(String[] args) {
        LogUtil.log.info("服务器启动了");
        SpringApplication.run(MainecraftApplication.class, args);
    }

}
