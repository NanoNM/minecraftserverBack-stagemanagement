package minecraftserveradmin.core.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value =Exception.class)
    public String exceptionHandler(Exception exception) throws IOException, URISyntaxException {
        return exception.getMessage();
    }
}


