package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.entity.FileModel;
import minecraftserveradmin.core.entity.NewFileModel;
import minecraftserveradmin.core.services.NewFileOperationService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewFIleOperationImpl implements NewFileOperationService {
    private static final String projectPath;
    String[] arrays = System.getProperty("user.dir").split("\\\\");
    String rootDir = (arrays[arrays.length-1]);
    static{
        projectPath  = System.getProperty("user.dir");
    }
    public List rootDir() throws IOException {
        List<NewFileModel> fileModelList = new ArrayList<>();
        File file = new File(projectPath);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中

        for(File f:fs){//遍历File[]数组
//            String rootPath = System.getProperty("user.dir");
            String s = f.getPath().replace(projectPath+File.separator,"");
            if(!f.isDirectory())		//若非目录(即文件)，则打印
            {
                String fileTyle = "";
                try {
                    fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                    NewFileModel fileModel = new NewFileModel(f.getName(),fileTyle,1,f.length(),null, null, s, null);
                    fileModelList.add(fileModel);
                }catch (Exception ignored){
                }
            }else {
                NewFileModel fileModel = new NewFileModel(f.getName(),"",0,0,null,null, s, null);
                fileModelList.add(fileModel);
            }
        }
        return fileModelList;
    }

    public List<NewFileModel> getFIle(String name) throws IOException {
        List<NewFileModel> fileModelList = new ArrayList<>();
        File file = new File(name);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs) {
            String s = f.getPath().replace(projectPath,"");//遍历File[]数组
            if (!f.isDirectory())        //若非目录(即文件)，则打印
            {
                String fileTyle = "";
                try {
                    fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                }catch (Exception ignored){
                }
                NewFileModel fileModel = new NewFileModel(f.getName(),fileTyle,1,f.length(),getFather(f.getPath()), null, s, null);
                fileModelList.add(fileModel);
            }else {
                NewFileModel fileModel = new NewFileModel(f.getName(),"",0,0,getFather(f.getPath()),null, s,  null);
                fileModelList.add(fileModel);
            }
        }
        return fileModelList;
    }

    private String getFather(String path) {
        File file = new File(path);
        String fatherName = file.getParentFile().getAbsolutePath();
        String s = fatherName.replace(projectPath+File.separator,"");
        return s;
    }
}
