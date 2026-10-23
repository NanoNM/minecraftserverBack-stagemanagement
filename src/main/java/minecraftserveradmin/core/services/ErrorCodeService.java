package minecraftserveradmin.core.services;

import minecraftserveradmin.core.util.ErrorCode;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ErrorCodeService {

    private static final Map<String, Object> ERROR_CODE_MAP = new LinkedHashMap<>();

    static {
        try {
            for (Field field : ErrorCode.class.getFields()) {
                ERROR_CODE_MAP.put(field.getName(), field.get(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回错误码映射，由 Jackson 序列化，避免手工拼接 JSON 带来的转义问题。
     */
    public static Map<String, Object> getErrorCodeMap() {
        return ERROR_CODE_MAP;
    }
}
