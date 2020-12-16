package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.GetModListService;
import minecraftserveradmin.core.services.SaveFileService;
import minecraftserveradmin.core.services.impl.SaveAsModImpl;
import minecraftserveradmin.core.util.ErrorCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ModSettingContorller {
    @Autowired
    GetModListService getModList;
    @Autowired
    SaveAsModImpl saveAsMod;
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
    public int uploadFile(@RequestParam("file") MultipartFile file) {
        if (file!= null && file.isEmpty()){
            return ErrorCode.NO_FILE_UPDATE;
        }
        try {
            assert file != null;
            saveAsMod.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ErrorCode.FILE_UPDATE_SUCCESS;
    }


}

