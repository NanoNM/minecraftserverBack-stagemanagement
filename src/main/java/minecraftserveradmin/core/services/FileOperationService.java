package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.FileModel;

import java.io.IOException;
import java.util.List;

public interface FileOperationService {
    List<FileModel> getFIle(String name) throws IOException;
    List<FileModel> rootDir() throws IOException;

}
