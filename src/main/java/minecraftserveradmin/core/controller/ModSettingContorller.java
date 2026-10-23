package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.dto.ResultBody;
import minecraftserveradmin.core.entity.ModModel;
import minecraftserveradmin.core.services.GetModListService;
import minecraftserveradmin.core.services.SaveFileService;
import minecraftserveradmin.core.services.impl.SaveAsModImpl;
import minecraftserveradmin.core.util.ErrorCode;
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
    public ResultBody<List<ModModel>> enumMod(@RequestParam("name") String adminName, @RequestParam(value = "cmd", required = false) String cmd,@RequestParam(value = "filename", required = false) String filename){
        if (cmd == null){
            return ResultBody.success(getModList.doScan());
        }else if("able".equals(cmd)){
            getModList.able(filename);
        }else if("disable".equals(cmd)) {
            getModList.disable(filename);
        }else if("remove".equals(cmd)){
            getModList.remove(filename);
        }else if("completelyDelete".equals(cmd)){
            getModList.CPRemove(adminName, filename);
        }
        return ResultBody.success(null);
    }

    //mod删除
    @ResponseBody
    @GetMapping("/admin/reMove")
    public ResultBody<String> removeMod(@RequestParam("cmd") String cmd,@RequestParam("filename") String filename){
        return ResultBody.error(ErrorCode.API_DEPRECATED, "功能未实现");
    }

    //mod信息
    @ResponseBody
    @GetMapping("/admin/moremodinfo")
    public ResultBody<String> removeMod(@RequestParam("filename") String filename){
        return ResultBody.success(getModList.moreModInfo(filename));
    }

    //mod上传
    @ResponseBody
    @PostMapping("/admin/upfilemods")
    public ResultBody<Integer> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file != null && file.isEmpty()){
            return ResultBody.error(ErrorCode.NO_FILE_UPDATE, "上传文件为空");
        }
        try {
            assert file != null;
            saveAsMod.save(file);
        } catch (Exception e) {
            return ResultBody.error(ErrorCode.NO_FILE_UPDATE, "上传失败: " + e.getMessage());
        }
        return ResultBody.success(ErrorCode.FILE_UPDATE_SUCCESS, ErrorCode.FILE_UPDATE_SUCCESS);
    }


}

