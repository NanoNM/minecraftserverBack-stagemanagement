package minecraftserveradmin.core.controller;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.entity.ServerSettingModel;
import minecraftserveradmin.core.services.FormatServerSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


@Controller
public class ServerInfoEditController {
    FormatServerSettingService formatServerSettingService = new FormatServerSettingService();
    @ResponseBody
    @GetMapping("/admin/getsetting")
    public ServerSettingModel infoEdit() throws IOException {
        return formatServerSettingService.getModel();
    }
    @ResponseBody
    @RequestMapping("/admin/setsetting")
    public boolean setSetting(@RequestBody JSONObject jsonParam) throws IOException {
        formatServerSettingService.settingFormat(jsonParam);
        return true;
    }
}
