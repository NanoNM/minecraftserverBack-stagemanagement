package minecraftserveradmin.core.services;

import minecraftserveradmin.core.util.ErrorCode;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class ErrorCodeService {
    static StringBuffer errorCodeBuffer = new StringBuffer();
    static {
        GeErrorCodeService();
    }
    private static void GeErrorCodeService(){
        errorCodeBuffer.append("{\"info\": \"errorCode\",\"codes\":{");
        Field[] fields = ErrorCode.class.getFields();
        for (int i = 0;i<fields.length;i++){
//            errorCodeBuffer.append("\"").append(getConst(fields[i].getName())).append("\":\"").append(fields[i].getName()).append("\"");
            errorCodeBuffer.append("\"").append(fields[i].getName()).append("\":\"").append(getConst(fields[i].getName())).append("\"");
            if (i != fields.length-1){
                errorCodeBuffer.append(",");
            }
        }
        errorCodeBuffer.append("}}");
    }

    private static Object getConst(String field){
        try {
            return ErrorCode.class.getField(field).get(null);
        } catch (Exception e) {}
        return null;
    }

    public static String getErrorCodeBuffer() {
        return errorCodeBuffer.toString();
    }
}
