package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.impl.NewFIleOperationImpl;
import minecraftserveradmin.core.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class FileController {

//  新的文件类避免用户隐私泄露
    @Autowired
    NewFIleOperationImpl fIleOperation;
    @ResponseBody
    @GetMapping("/admin/file")
    private List fileManger(@RequestParam(value = "path",required = false) String path){
        if (path == null){
            return fIleOperation.rootDir();
        }else{
            try {
                return fIleOperation.getFIle(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    @ResponseBody
    @GetMapping("/admin/renameFile")
    private Integer fileRename(@RequestParam(value = "path") String path,@RequestParam(value = "rename")String name){
        if (fIleOperation.renameFile(path, name)){
            return ErrorCode.FILE_RENAME_SUCCESS;
        }
        return ErrorCode.FILE_RENAME_ERROR;
    }
    @ResponseBody
    @GetMapping("/admin/download")
    private void fileDownloader(@RequestParam(value = "path") String path,HttpServletRequest request, HttpServletResponse response){
        try {
            fIleOperation.fileDownloader(path, request, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @ResponseBody
    @PostMapping("/admin/fileeditor")
    private Integer fileEditor(@RequestParam(value = "path") String path,@RequestParam(value = "string") String str){
        if(fIleOperation.fileEditor(path, str)){
            return ErrorCode.FILE_EDIT_SUCCES;
        }
        return ErrorCode.FILE_EDIT_FAIL;
    }
    @ResponseBody
    @GetMapping("/admin/filedelete")
    private Integer fileDelete(@RequestParam(value = "path") String path){
        if(fIleOperation.fileDelete(path)){
            return ErrorCode.FILE_REMOVE_SUCCES;
        }
        return ErrorCode.FILE_REMOVE_FILE;
    }
    @ResponseBody
    @GetMapping("/admin/mkdir")
    private Integer mkdir(@RequestParam(value = "path") String path){
        return fIleOperation.mkdir(path);
    }
    @ResponseBody
    @GetMapping("/admin/makefile")
    private Integer makeFile(@RequestParam(value = "path") String path){
        return fIleOperation.makeFile(path);
    }
    @ResponseBody
    @GetMapping("/admin/filecopy")
    private Integer FileCopy(
            @RequestParam(value = "source") String source,
            @RequestParam(value = "dest") String dest){
//        return fIleOperation.makeFile(path);
        return fIleOperation.fileCopy(source, dest);
    }
    @ResponseBody
    @GetMapping("/admin/filemove")
    private Integer FileMove(
            @RequestParam(value = "source") String source,
            @RequestParam(value = "dest") String dest){
//        return fIleOperation.makeFile(path);
        return fIleOperation.fileMove(source, dest);
    }
}
