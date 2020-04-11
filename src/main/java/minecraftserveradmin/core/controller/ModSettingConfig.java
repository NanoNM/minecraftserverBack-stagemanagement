package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.GetModListService;
import minecraftserveradmin.core.services.SaveAsModService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Controller
public class ModSettingConfig {
    @Autowired
    GetModListService getModList;
    @Autowired
    SaveAsModService saveAsModService;
    //mod启用禁用
    @ResponseBody
    @GetMapping("/admin/modScanning")
    public List enumMod(@Param("cmd") String cmd,@Param("filename") String filename){
        if (cmd == null){
            return getModList.doScan();
        }else if("able".equals(cmd)){
            getModList.able(filename);
        }else if("disable".equals(cmd)) {
            getModList.disable(filename);
        }else if("remove".equals(cmd)){
            getModList.remove(filename);
        }
        return null;
    }

    //mod删除
    @ResponseBody
    @GetMapping("/admin/reMove")
    public String removeMod(@Param("cmd") String cmd,@Param("filename") String filename){
        return "功能未实现！！";
    }

    //mod信息
    @ResponseBody
    @GetMapping("/admin/moremodinfo")
    public String removeMod(@Param("filename") String filename){
        return getModList.moreModInfo(filename);
    }

    //mod上传
    @ResponseBody
    @PostMapping("/admin/upfilemods")
    public String uploadFile(@RequestParam("filename") MultipartFile file) {
        if (file!= null && file.isEmpty()){
            return "error 没有选择文件？";
        }
        try {
            saveAsModService.savemod(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success !!";
    }


}

