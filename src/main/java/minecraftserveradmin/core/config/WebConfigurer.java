package minecraftserveradmin.core.config;

import minecraftserveradmin.core.config.intercepors.AdminOperationIntercept;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfigurer implements WebMvcConfigurer {
    @Autowired
    private AdminOperationIntercept adminOperationIntercept;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns("/**") 表示拦截所有的请求，
        // excludePathPatterns("/login", "/register") 表示除了登陆与注册之外，因为登陆注册不需要登陆也可以访问
        registry.addInterceptor(adminOperationIntercept).addPathPatterns("admin/**");
    //.excludePathPatterns("admin/login", "admin/register");
        //较新Spring Boot的版本中这里可以直接去掉，否则会报错
    }
}
