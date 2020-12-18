package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.services.SaveFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 保存为mod
 */
@Service
public class SaveAsModImpl implements SaveFileService {
    @Override
    public boolean save(MultipartFile file) {
        try {
            String mainpath = System.getProperty("user.dir");
            File file1 = new File(mainpath+"/mods/"+file.getOriginalFilename());
            file.transferTo(file1);//保存文件
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
