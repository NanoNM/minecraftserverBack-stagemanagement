package minecraftserveradmin.core.dto;

import minecraftserveradmin.core.util.ErrorCode;

/**
 * 统一响应体。所有 HTTP 接口（文件下载等二进制流除外）均返回该结构。
 *
 * @param <T> 业务数据类型
 */
public class ResultBody<T> {

    /** 业务结果码，0 表示成功，其余见 {@link ErrorCode} */
    private Integer resultCode;
    private String message;
    private T object;

    public ResultBody() {
    }

    public ResultBody(Integer resultCode, String message, T object) {
        this.resultCode = resultCode;
        this.message = message;
        this.object = object;
    }

    public static <T> ResultBody<T> success(T data) {
        return new ResultBody<>(ErrorCode.SUCCESS, "success", data);
    }

    public static <T> ResultBody<T> success(Integer code, T data) {
        return new ResultBody<>(code, "success", data);
    }

    public static <T> ResultBody<T> error(Integer code, String message) {
        return new ResultBody<>(code, message, null);
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
