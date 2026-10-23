package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.dto.ResultBody;
import minecraftserveradmin.core.services.ErrorCodeService;
import minecraftserveradmin.core.services.GetServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 主页控制层 控制网站的主页请求和主页相关事宜请求
 */

@Controller
public class MainPageController {

    @Autowired
    GetServerInfoService getServerInfoService;

    /**
     * 返回 Thymeleaf 视图名，不包装为 ResultBody。
     */
    @GetMapping("/")
    private String index(){
        return "index";
    }

    @ResponseBody
    @GetMapping("/errorcode")
    private ResultBody<Map<String, Object>> getErrcode(){
        return ResultBody.success(ErrorCodeService.getErrorCodeMap());
    }

}
