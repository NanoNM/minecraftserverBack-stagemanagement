package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.FileModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FileOperationService {
    void getFIle(String name);
    List rootDir() throws IOException;
}
