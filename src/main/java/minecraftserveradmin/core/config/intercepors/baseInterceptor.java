package minecraftserveradmin.core.config.intercepors;

import org.apache.catalina.User;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public interface baseInterceptor extends HandlerInterceptor {
    //这个方法是在访问接口之前执行的，我们只需要在这里写验证登陆状态的业务逻辑，就可以在用户调用指定接口之前验证登陆状态了
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);

    void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView);

    void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex);

}
