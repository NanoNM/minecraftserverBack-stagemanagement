package minecraftserveradmin.core.config.intercepors;

import minecraftserveradmin.core.controller.WebSocketService;
import minecraftserveradmin.core.entity.AOPtoken;
import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class AdminOperationIntercept implements baseInterceptor{
    @Override
    public boolean preHandle(@RequestParam HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())){
            return true;
        }
        String name = request.getParameter("name");
        String token = request.getParameter("token");
        String jsonstr =  request.getParameter("jsonstr");
        // 为了方便调试
        if ("233".equals(name) && "233".equals(token)){
            return true;
        }
        for (AOPtoken aoPtoken : WebSocketService.AOPtokens) {
            if(aoPtoken.getName().equals(name)){
                if (aoPtoken.getToken().equals(token)){
                    TokenUtil.getNewToken(aoPtoken.getSession(), WebSocketService.AOPtokens);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
