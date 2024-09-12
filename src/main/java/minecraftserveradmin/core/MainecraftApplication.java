package minecraftserveradmin.core;

import minecraftserveradmin.core.util.LogUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;


@MapperScan(value = "minecraftserveradmin.core.dao")
@SpringBootApplication
public class MainecraftApplication {
    public static void main(String[] args) {
        LogUtil.log.info("服务器启动了");
        SpringApplication.run(MainecraftApplication.class, args);
    }

    /**
     * 接受对特殊字符的请求
     * @return
     */
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "|{}[]\\"));
        return factory;
    }

}