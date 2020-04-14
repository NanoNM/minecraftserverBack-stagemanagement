package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.entity.FileModel;
import minecraftserveradmin.core.services.FileOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FIleOperationImpl implements FileOperationService {

    private static final String projectPath;
    static{
        projectPath  = System.getProperty("user.dir");
    }
    @Override
    public List rootDir() throws IOException {
        List<FileModel> fileModelList = new ArrayList<>();
        File file = new File(projectPath);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs){					//遍历File[]数组
            if(!f.isDirectory())		//若非目录(即文件)，则打印
            {
                String fileTyle = "";
                try {
                    fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                }catch (Exception ignored){
                }
                FileModel fileModel = new FileModel(f.getName(),fileTyle,1,f.length(),getFather(f.getPath()), DigestUtils.md5Digest(new FileInputStream(f)));
                fileModelList.add(fileModel);
            }else {
                FileModel fileModel = new FileModel(f.getName(),"",0,0,getFather(f.getPath()),null);
                fileModelList.add(fileModel);
            }
        }
        return fileModelList;
    }
    @Override
    public void getFIle(String name) {

    }

    private FileModel getFather(String name) throws IOException {
        File file = new File(name);
        if (projectPath.substring(projectPath.lastIndexOf("/")).equals("/"+file.getParentFile().getName())){
            return null;
        }else{
            File FatherFile = new File(file.getParentFile().getPath());
            FileModel fileModel = new FileModel(FatherFile.getName(),"",0,0,getFather(FatherFile.getCanonicalPath()),DigestUtils.md5Digest(new FileInputStream(FatherFile)));
            return fileModel;
        }
    }
}
