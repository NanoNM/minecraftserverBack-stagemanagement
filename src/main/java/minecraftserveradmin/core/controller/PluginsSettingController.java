package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.GetPluginsListService;
import minecraftserveradmin.core.services.SaveFileService;
import minecraftserveradmin.core.services.impl.SaveAsPluginImpl;
import org.apache.ibatis.annotations.Param;
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
    //mod启用禁用
    @ResponseBody
    @GetMapping("/admin/pluginScanning")
    public List enumMod(@Param("cmd") String cmd,@Param("filename") String filename){
        if (cmd == null){
            return getPluginsListService.doScan();
        }else if("able".equals(cmd)){
            getPluginsListService.able(filename);
        }else if("disable".equals(cmd)) {
            getPluginsListService.disable(filename);
        }else if("remove".equals(cmd)){
            getPluginsListService.remove(filename);
        }
        return null;
    }

    //mod删除
    @ResponseBody
    @GetMapping("/admin/repluginMove")
    public String removeMod(@Param("cmd") String cmd,@Param("filename") String filename){
        return "功能未实现！！";
    }

    //mod信息
    @ResponseBody
    @GetMapping("/admin/moreplugininfo")
    public Map<String, Object> removeMod(@Param("filename") String filename){
        return getPluginsListService.morePluginInfo(filename);
    }

    //mod上传
    @ResponseBody
    @PostMapping("/admin/upfileplugins")
    public String uploadFile(@RequestParam("filename") MultipartFile file) {
        if (file!= null && file.isEmpty()){
            return "error 没有选择文件？";
        }
        try {
            saveAsPlugin.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success !!";
    }


}

