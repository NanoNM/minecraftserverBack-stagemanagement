package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.dto.ResultBody;
import minecraftserveradmin.core.entity.PluginModel;
import minecraftserveradmin.core.services.GetPluginsListService;
import minecraftserveradmin.core.services.impl.SaveAsPluginImpl;
import minecraftserveradmin.core.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class PluginsSettingController {
    @Autowired
    GetPluginsListService getPluginsListService;
    @Autowired
    SaveAsPluginImpl saveAsPlugin;

    //插件启用禁用
    @ResponseBody
    @GetMapping("/admin/pluginScanning")
    public ResultBody<List<PluginModel>> enumMod(@RequestParam("name") String adminName, @RequestParam(value = "cmd", required = false) String cmd,@RequestParam(value = "filename", required = false) String filename){
        if (cmd == null){
            return ResultBody.success(getPluginsListService.doScan());
        }else if("able".equals(cmd)){
            getPluginsListService.able(filename);
        }else if("disable".equals(cmd)) {
            getPluginsListService.disable(filename);
        }else if("remove".equals(cmd)){
            getPluginsListService.remove(filename);
        }else if("completelyDelete".equals(cmd)){
            getPluginsListService.CPRemove(adminName, filename);
        }
        return ResultBody.success(null);
    }

    //插件删除
    @ResponseBody
    @GetMapping("/admin/repluginMove")
    public ResultBody<String> removeMod(@RequestParam("cmd") String cmd,@RequestParam("filename") String filename){
        return ResultBody.error(ErrorCode.API_DEPRECATED, "功能未实现");
    }

    //插件信息
    @ResponseBody
    @GetMapping("/admin/moreplugininfo")
    public ResultBody<Map<String, Object>> removeMod(@RequestParam("filename") String filename){
        return ResultBody.success(getPluginsListService.morePluginInfo(filename));
    }

    //插件上传
    @ResponseBody
    @PostMapping("/admin/upfileplugins")
    public ResultBody<Integer> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file != null && file.isEmpty()){
            return ResultBody.error(ErrorCode.NO_FILE_UPDATE, "上传文件为空");
        }
        try {
            saveAsPlugin.save(file);
        } catch (Exception e) {
            return ResultBody.error(ErrorCode.NO_FILE_UPDATE, "上传失败: " + e.getMessage());
        }
        return ResultBody.success(ErrorCode.FILE_UPDATE_SUCCESS, ErrorCode.FILE_UPDATE_SUCCESS);
    }


}

