package minecraftserveradmin.core.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class SaveAsModService {
    public void savemod(MultipartFile file) {
        try {
            String mainpath = System.getProperty("user.dir");
            File file1 = new File(mainpath+"\\mods\\"+file.getOriginalFilename());
            file.transferTo(file1);//保存文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
