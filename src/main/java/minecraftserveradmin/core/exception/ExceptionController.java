package minecraftserveradmin.core.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.net.URLEncoder;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value =Exception.class)
    public String exceptionHandler(Exception exception) throws IOException {
//        java.net.URI uri = java.net.URI.create("https://www.baidu.com/s?wd=" + URLEncoder.encode(exception.getMessage(),"utf-8"));
//        // 获取当前系统桌面扩展
//        java.awt.Desktop dp = java.awt.Desktop.getDesktop();
//        // 判断系统桌面是否支持要执行的功能
//        if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
//            // 获取系统默认浏览器打开链接
//            dp.browse(uri);
//        }
        return exception.getMessage();
    }
}


