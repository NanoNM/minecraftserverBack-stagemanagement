package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.services.SaveFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 保存为插件
 */
@Service
public class SaveAsPluginImpl implements SaveFileService {
    @Override
    public void save(MultipartFile file) {
        try {
            String mainpath = System.getProperty("user.dir");
            File file1 = new File(mainpath+"/plugins/"+file.getOriginalFilename());
            file.transferTo(file1);//保存文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
