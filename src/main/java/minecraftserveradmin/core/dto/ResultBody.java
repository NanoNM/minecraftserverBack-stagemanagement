package minecraftserveradmin.core.dto;

public class ResultBody {
    private Integer resultCode;
    private String  message;
    private Object object;

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

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ResultBody(Integer resultCode, String message, Object object) {
        this.resultCode = resultCode;
        this.message = message;
        this.object = object;
    }

    public ResultBody() {

    }
}
