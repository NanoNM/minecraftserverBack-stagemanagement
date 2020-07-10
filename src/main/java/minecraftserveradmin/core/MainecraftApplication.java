package minecraftserveradmin.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan(value = "minecraftserveradmin.core.dao")
@SpringBootApplication
public class MainecraftApplication {

    public static void main(String[] args) {
        //LogUtil.log.info("服务器启动了");
        SpringApplication.run(MainecraftApplication.class, args);
    }

}
