package minecraftserveradmin.core.dto;

public class SocketResult {

    public SocketResult() {
    }
    public SocketResult(String name, String message, Object object) {
        this.name = name;
        this.message = message;
        this.object = object;
    }



    private String name;
    private String message;
    private Object object;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
