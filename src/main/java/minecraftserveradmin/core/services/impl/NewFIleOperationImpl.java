package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.entity.NewFileModel;
import minecraftserveradmin.core.services.NewFileOperationService;
import minecraftserveradmin.core.util.CharsetUtil;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.GCUtil;
import minecraftserveradmin.core.util.LogUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewFIleOperationImpl implements NewFileOperationService {
    private static final String projectPath;
    /** 项目根目录的规范化绝对路径，所有文件操作都必须限制在该目录内 */
    private static final Path rootPath;
    static{
        projectPath  = System.getProperty("user.dir");
        rootPath = Paths.get(projectPath).toAbsolutePath().normalize();
    }

    /**
     * 将客户端传入的路径解析为项目根目录内的绝对路径。
     * <p>
     * 兼容前端返回的带前导分隔符的相对路径（如 {@code /src/main}）：
     * 这类路径在 Windows 上会被 {@link Paths#get} 解析为盘符根目录下的路径，
     * 因此先去掉前导分隔符再按项目根目录解析。
     * 绝对路径必须位于项目根目录内；通过 {@code normalize()} 消除 {@code ..}
     * 等相对片段后再做前缀校验，防止越出项目目录。
     *
     * @param path 客户端传入的路径
     * @return 规范化后的绝对路径；路径为空、非法或越出项目根目录时返回 null
     */
    private Path resolveSafePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        try {
            Path candidate = Paths.get(path);
            Path resolved;
            if (candidate.isAbsolute()) {
                resolved = candidate.normalize();
            } else {
                // 去掉前导分隔符，统一按项目根目录解析
                String relative = path.replace('\\', '/');
                while (relative.startsWith("/")) {
                    relative = relative.substring(1);
                }
                resolved = rootPath.resolve(relative).normalize();
            }
            if (!resolved.startsWith(rootPath)) {
                LogUtil.log.warn("拒绝越界路径访问: " + path);
                return null;
            }
            return resolved;
        } catch (InvalidPathException e) {
            LogUtil.log.warn("非法路径: " + path);
            return null;
        }
    }

    public int renameFile(){
        return 1;
    }

    public boolean renameFile(String path, String name){
        Path oldPath = resolveSafePath(path);
        Path newPath = resolveSafePath(name);
        if (oldPath == null || newPath == null) {
            return false;
        }
        try {
            if (reNameFile(oldPath, newPath))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void fileDownloader(String string, HttpServletRequest request, HttpServletResponse httpResponse) throws UnsupportedEncodingException {
        Path safePath = resolveSafePath(string);
        if (safePath == null) {
            return;
        }
        File file = safePath.toFile();
        if (file.exists()) {
            httpResponse.setHeader("content-type", "application/octet-stream" + ";charset=" + CharsetUtil.guessCharacterSet(file));
            httpResponse.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpResponse.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            httpResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));

            // 实现文件下载
            byte[] buffer = new byte[4096];
//            FileInputStream fis = null;
//            BufferedInputStream bis = null;
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)){
                OutputStream os = httpResponse.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                GCUtil.doGC();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List rootDir() {
        List<NewFileModel> fileModelList = new ArrayList<>();
        File file = new File(projectPath);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中

        for(File f:fs){
            // 遍历File[]数组
            // String rootPath = System.getProperty("user.dir");
            String s = f.getPath().replace(projectPath+File.separator,"");
            if(!f.isDirectory()){
                String fileTyle = "";
                try {
                    try{
                        fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                    }catch (StringIndexOutOfBoundsException se){
                        fileTyle = null;
                    }
                    NewFileModel fileModel = new NewFileModel(f.getName(),fileTyle,1,f.length(),null, null, s, null);
                    fileModelList.add(fileModel);
                }catch (Exception e){
                    e.printStackTrace();
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
        Path safePath = resolveSafePath(name);
        if (safePath == null) {
            return fileModelList;
        }
        File file = safePath.toFile();
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        if (fs == null) {
            return fileModelList;
        }
        for(File f:fs) {
            String s = f.getPath().replace(projectPath,"");//遍历File[]数组

            if (!f.isDirectory())        //若非目录(即文件)，则打印
            {
                String fileTyle = "";
                try {
                    fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                }catch (Exception ignored){
                    fileTyle = null;
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

    private boolean reNameFile(Path oldPath, Path newPath) throws IOException {
        File oldName = oldPath.toFile();
        File newName = newPath.toFile();
        if (newName.exists()) {
            throw new java.io.IOException("文件已存在" + newName);
        }
        if(oldName.renameTo(newName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean fileEditor(String path, String str){
        Path safePath = resolveSafePath(path);
        if (safePath == null) {
            return false;
        }
        File f = safePath.toFile();
        try (OutputStream os = new FileOutputStream(f);){
            os.write(str.getBytes());
            os.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean fileDelete(String path) {
        Path safePath = resolveSafePath(path);
        if (safePath == null) {
            return false;
        }
        // 禁止删除项目根目录本身
        if (safePath.equals(rootPath)) {
            LogUtil.log.warn("拒绝删除项目根目录");
            return false;
        }
        return doDelete(safePath.toFile());
    }
    private boolean doDelete(File f){
        if (f.isDirectory()){
            boolean flag = false;
            File[] fs = f.listFiles();
            if (fs!=null){
                for (File file:fs){
                    if (file.isDirectory()){
                        doDelete(file);
                    }
                    flag = file.delete();
                }
            }else{
                return f.delete();
            }
            if (flag){
                return f.delete();
            }
        }
        if(f.exists()) {
            return f.delete();
        }
        return false;
    }

    public Integer mkdir(String path) {
        Path safePath = resolveSafePath(path);
        if (safePath == null) {
            return ErrorCode.FOLDER_CREATED_FAIL;
        }
        File f = safePath.toFile();
        if(f.exists()){
            return ErrorCode.FOLDER_ALREADY_EXISTS;
        }
        boolean flag = f.mkdir();
        if (flag){
            return ErrorCode.FOLDER_CREATED_SUCCESSFULLY;
        }
        return ErrorCode.FOLDER_CREATED_FAIL;
    }

    public Integer makeFile(String path) {
        Path safePath = resolveSafePath(path);
        if (safePath == null) {
            return ErrorCode.FILE_CREATED_FAIL;
        }
        File f = safePath.toFile();
        if(f.exists()){
            return ErrorCode.FILE_ALREADY_EXISTS;
        }
        boolean flag = false;
        try {
            flag = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.FILE_CREATED_FAIL;
        }
        if (flag){
            return ErrorCode.FILE_CREATED_SUCCESSFULLY;
        }
        return ErrorCode.FILE_CREATED_FAIL;
    }

    public Integer fileCopy(String source, String dest) {
        Path safeSource = resolveSafePath(source);
        Path safeDest = resolveSafePath(dest);
        if (safeSource == null || safeDest == null) {
            return ErrorCode.FILE_COPY_FAIL;
        }
        try {
            Files.copy(safeSource, safeDest);
            return ErrorCode.FILE_COPY_SUCCESS;
        } catch (FileAlreadyExistsException e) {
            return ErrorCode.FILE_ALREADY_EXISTS;
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.FILE_COPY_FAIL;
        }
    }

    public Integer fileMove(String source, String dest) {
        Path safeSource = resolveSafePath(source);
        Path safeDest = resolveSafePath(dest);
        if (safeSource == null || safeDest == null) {
            return ErrorCode.FILE_MOVE_FAIL;
        }
        try {
            Files.move(safeSource, safeDest);
            return ErrorCode.FILE_MOVE_SUCCESS;
        } catch (FileAlreadyExistsException e) {
            return ErrorCode.FILE_ALREADY_EXISTS;
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.FILE_MOVE_FAIL;
        }
    }
}
