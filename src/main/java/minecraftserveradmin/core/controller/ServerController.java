package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.dto.ResultBody;
import minecraftserveradmin.core.services.FormatServerSettingService;
import minecraftserveradmin.core.services.GetServerInfoService;
import minecraftserveradmin.core.services.RunServerService;
import minecraftserveradmin.core.util.ErrorCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;

@Controller
public class ServerController {

    @Autowired
    RunServerService runServerService;

    @Autowired
    GetServerInfoService getServerInfoService;

    @Autowired
    FormatServerSettingService formatServerSettingService;

    /**
     *
     * @param cmd 命令
     * @return 服务器指令传递
     */
    @ResponseBody
    @GetMapping("/admin/server/s")
    @Deprecated
    private ResultBody<String> index(@Param("cmd") String cmd) {
        return ResultBody.error(ErrorCode.API_DEPRECATED, "接口废弃");
    }

    @ResponseBody
    @GetMapping("/admin/getjar")
    private ResultBody<List<String>> getJar() {
        try {
            return ResultBody.success(getServerInfoService.getJar());
        } catch (UnsupportedEncodingException e) {
            return ResultBody.error(ErrorCode.SYSTEM_ERROR, "获取 jar 列表失败: " + e.getMessage());
        }
    }

    /**
     *
     * @return string 控制台log信息
     */
    @ResponseBody
    @GetMapping("/admin/server")
    private ResultBody<String> serverInfoGetter(){
        return ResultBody.error(ErrorCode.API_DEPRECATED, "接口废弃");
    }

    /**
     * 服务器状态获取
     * @return 0关机 1开始
     */
    @ResponseBody
    @GetMapping("/admin/serverTest")
    @Deprecated
    private ResultBody<String> serverTest(){
        return ResultBody.error(ErrorCode.API_DEPRECATED, "接口废弃");
    }

    /**
     * 服务器当前设置
     * @return 服务器配置项
     */
    @ResponseBody
    @GetMapping("/admin/getsetting")
    public ResultBody<String> infoEdit() throws IOException {
        return ResultBody.success(formatServerSettingService.getModel());
    }

    /**
     *
     * @param jsonParam json字符串
     * @return 是否保存成功
     */
    @ResponseBody
    @GetMapping("/admin/setsetting")
    public ResultBody<Boolean> setSetting(@RequestParam("jsonstr") String jsonParam) {
        try {
            formatServerSettingService.settingFormat(jsonParam);
            return ResultBody.success(true);
        } catch (Exception exception) {
            return ResultBody.error(ErrorCode.SYSTEM_ERROR, "保存服务器配置失败: " + exception.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/admin/synchronizationConsole")
    public ResultBody<String> synchronizationConsole() throws IOException {
        return ResultBody.success(getServerInfoService.synchronizationConsole());
    }
}


