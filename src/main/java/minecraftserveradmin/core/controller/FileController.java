package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.dto.ResultBody;
import minecraftserveradmin.core.entity.NewFileModel;
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
    private ResultBody<List<NewFileModel>> fileManger(@RequestParam(value = "path",required = false) String path){
        if (path == null){
            return ResultBody.success(fIleOperation.rootDir());
        }
        try {
            return ResultBody.success(fIleOperation.getFIle(path));
        } catch (IOException e) {
            return ResultBody.error(ErrorCode.FILE_EDIT_FAIL, "读取目录失败: " + e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/admin/renameFile")
    private ResultBody<Integer> fileRename(@RequestParam(value = "path") String path,@RequestParam(value = "rename")String name){
        if (fIleOperation.renameFile(path, name)){
            return ResultBody.success(ErrorCode.FILE_RENAME_SUCCESS, ErrorCode.FILE_RENAME_SUCCESS);
        }
        return ResultBody.error(ErrorCode.FILE_RENAME_ERROR, "重命名失败");
    }

    /**
     * 文件下载直接写出二进制流，无法包装为 ResultBody。
     */
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
    private ResultBody<Integer> fileEditor(@RequestParam(value = "path") String path,@RequestParam(value = "string") String str){
        if(fIleOperation.fileEditor(path, str)){
            return ResultBody.success(ErrorCode.FILE_EDIT_SUCCES, ErrorCode.FILE_EDIT_SUCCES);
        }
        return ResultBody.error(ErrorCode.FILE_EDIT_FAIL, "文件保存失败");
    }

    @ResponseBody
    @GetMapping("/admin/filedelete")
    private ResultBody<Integer> fileDelete(@RequestParam(value = "path") String path){
        if(fIleOperation.fileDelete(path)){
            return ResultBody.success(ErrorCode.FILE_REMOVE_SUCCES, ErrorCode.FILE_REMOVE_SUCCES);
        }
        return ResultBody.error(ErrorCode.FILE_REMOVE_FILE, "文件删除失败");
    }

    @ResponseBody
    @GetMapping("/admin/mkdir")
    private ResultBody<Integer> mkdir(@RequestParam(value = "path") String path){
        return wrap(fIleOperation.mkdir(path));
    }

    @ResponseBody
    @GetMapping("/admin/makefile")
    private ResultBody<Integer> makeFile(@RequestParam(value = "path") String path){
        return wrap(fIleOperation.makeFile(path));
    }

    @ResponseBody
    @GetMapping("/admin/filecopy")
    private ResultBody<Integer> FileCopy(
            @RequestParam(value = "source") String source,
            @RequestParam(value = "dest") String dest){
        return wrap(fIleOperation.fileCopy(source, dest));
    }

    @ResponseBody
    @GetMapping("/admin/filemove")
    private ResultBody<Integer> FileMove(
            @RequestParam(value = "source") String source,
            @RequestParam(value = "dest") String dest){
        return wrap(fIleOperation.fileMove(source, dest));
    }

    /**
     * 服务层以 ErrorCode 表达结果：10xx 为成功，3xxx 为失败，0 表示无权限。
     */
    private ResultBody<Integer> wrap(Integer code) {
        if (code != null && code >= 1000 && code < 3000) {
            return ResultBody.success(code, code);
        }
        return ResultBody.error(code, "操作失败");
    }
}
