package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.entity.ServerInfoModel;
import minecraftserveradmin.core.services.ErrorCodeService;
import minecraftserveradmin.core.services.GetServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 主页控制层 控制网站的主页请求和主页相关事宜请求
 */

@Controller
public class MainPageController {


    @Autowired
    GetServerInfoService getServerInfoService;
    @GetMapping("/")
    private String index(){
        return "index";
    }
    @ResponseBody
    @GetMapping("/admin/getServerInfo")
    private ServerInfoModel serverInfo(){
        return getServerInfoService.setModel();
    }
    @ResponseBody
    @GetMapping("/errorcode")
    private String getErrcode(){
        return ErrorCodeService.getErrorCodeBuffer();
    }

}
