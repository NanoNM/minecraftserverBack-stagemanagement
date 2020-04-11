package minecraftserveradmin.core.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传存储接口
 */
@Service
public interface SaveFileService {
    void save(MultipartFile file);
}
