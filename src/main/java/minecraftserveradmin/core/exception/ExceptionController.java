package minecraftserveradmin.core.exception;

import minecraftserveradmin.core.dto.ResultBody;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.LogUtil;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一异常处理。
 * <p>
 * 所有未捕获异常在此转换为统一的 {@link ResultBody} 结构返回，HTTP 状态码固定为 200，
 * 由 resultCode 表达业务结果，避免前端直接收到 500 错误页或异常堆栈。
 * 真实异常信息只写入服务端日志。
 */
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BusinessException.class)
    public ResultBody<Void> businessExceptionHandler(BusinessException exception, HttpServletRequest request) {
        LogUtil.log.warn("业务异常 [{}] {}: {}", exception.getCode(), request.getRequestURI(), exception.getMessage());
        return ResultBody.error(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public ResultBody<Void> paramExceptionHandler(Exception exception, HttpServletRequest request) {
        LogUtil.log.warn("请求参数错误 {}: {}", request.getRequestURI(), exception.getMessage());
        return ResultBody.error(ErrorCode.PARAM_ERROR, "请求参数错误: " + exception.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultBody<Void> methodNotSupportedHandler(HttpRequestMethodNotSupportedException exception,
                                                      HttpServletRequest request) {
        LogUtil.log.warn("请求方法不支持 {}: {}", request.getRequestURI(), exception.getMessage());
        return ResultBody.error(ErrorCode.REQUEST_METHOD_NOT_SUPPORTED, "请求方法不支持: " + exception.getMethod());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultBody<Void> mediaTypeNotSupportedHandler(HttpMediaTypeNotSupportedException exception,
                                                         HttpServletRequest request) {
        LogUtil.log.warn("请求内容类型不支持 {}: {}", request.getRequestURI(), exception.getMessage());
        return ResultBody.error(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED, "请求内容类型不支持");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultBody<Void> uploadSizeExceededHandler(MaxUploadSizeExceededException exception,
                                                      HttpServletRequest request) {
        LogUtil.log.warn("上传文件过大 {}: {}", request.getRequestURI(), exception.getMessage());
        return ResultBody.error(ErrorCode.UPLOAD_SIZE_EXCEEDED, "上传文件超过大小限制");
    }

    @ExceptionHandler(Exception.class)
    public ResultBody<Void> exceptionHandler(Exception exception, HttpServletRequest request,
                                             HttpServletResponse response) {
        LogUtil.log.error("系统异常 " + request.getRequestURI(), exception);
        // 响应已提交（例如文件流已开始写出）时无法再写入 JSON，直接返回避免二次异常
        if (response.isCommitted()) {
            return null;
        }
        return ResultBody.error(ErrorCode.SYSTEM_ERROR, "服务器内部错误，请稍后重试");
    }
}


