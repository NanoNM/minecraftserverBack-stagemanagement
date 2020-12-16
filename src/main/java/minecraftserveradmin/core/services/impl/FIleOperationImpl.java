package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.entity.FileModel;
import minecraftserveradmin.core.services.FileOperationService;
import minecraftserveradmin.core.services.GetServerInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FIleOperationImpl implements FileOperationService {
    GetServerInfoService getServerInfoService = new GetServerInfoService();
    public boolean testSystem(){
        return "Windows".equals(getServerInfoService.getSystem());
    }
    private static final String projectPath;
    static{
        projectPath  = System.getProperty("user.dir");
    }
    @Override
    public List rootDir() throws IOException {
        List<FileModel> fileModelList = new ArrayList<>();
        // /src/main/java/minecraftserveradmin/core/services/impl
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
                FileModel fileModel = new FileModel(f.getName(),fileTyle,1,f.length(),getFather(f.getPath()), DigestUtils.md5Digest(new FileInputStream(f)), f.getPath(), f.getPath());
                fileModelList.add(fileModel);
            }else {
                FileModel fileModel = new FileModel(f.getName(),"",0,0,getFather(f.getPath()),null, f.getPath(), f.getPath());
                fileModelList.add(fileModel);
            }
        }
        return fileModelList;
    }
    @Override
    public List<FileModel> getFIle(String name) throws IOException {
        List<FileModel> fileModelList = new ArrayList<>();
        File file = new File(name);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs) {                    //遍历File[]数组
            if (!f.isDirectory())        //若非目录(即文件)，则打印
            {
                String fileTyle = "";
                try {
                    fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                }catch (Exception ignored){
                }
                //FileModel fileModel = new FileModel(f.getName(),fileTyle,1,f.length(),getFather(f.getPath()), DigestUtils.md5Digest(new FileInputStream(f)), f.getPath(), f.getParent());
                FileModel fileModel = new FileModel(f.getName(),fileTyle,1,f.length(),null, DigestUtils.md5Digest(new FileInputStream(f)), f.getPath(), f.getParent());
                fileModelList.add(fileModel);
            }else {
                //FileModel fileModel = new FileModel(f.getName(),"",0,0,getFather(f.getPath()),null, f.getPath(),  f.getParent());
                FileModel fileModel = new FileModel(f.getName(),"",0,0,null,null, f.getPath(),  f.getParent());
                fileModelList.add(fileModel);
            }
        }
        return fileModelList;
    }

    private FileModel getFather(String name) throws IOException {
        File file = new File(name);
        FileModel fileModel;
        if(testSystem()){
            if (projectPath.substring(projectPath.lastIndexOf("\\")).equals("\\"+file.getParentFile().getName())){
                return null;
            }else{
                return getFileModel(file);
            }
        }else{
            if (projectPath.substring(projectPath.lastIndexOf("/")).equals("/"+file.getParentFile().getName())){
                return null;
            }else{
                return getFileModel(file);
            }
        }
    }

    private FileModel getFileModel(File file) throws IOException {
        FileModel fileModel;
        File FatherFile = new File(file.getParentFile().getPath());
        if (!FatherFile.isDirectory()){
            fileModel = new FileModel(FatherFile.getName(),"",0,0,getFather(FatherFile.getCanonicalPath()), DigestUtils.md5Digest(new FileInputStream(FatherFile)), FatherFile.getPath(), FatherFile.getParent());
        }else{
            fileModel = new FileModel(FatherFile.getName(),"",0,0,getFather(FatherFile.getCanonicalPath()),null, FatherFile.getPath(), FatherFile.getParent());
        }
        return fileModel;
    }
}
