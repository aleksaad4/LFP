package ad4si2.lfp.web.exceptions;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@ControllerAdvice(basePackages = "ad4si2.lfp.web.controllers")
public class LfpExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<JSONObject> exception(final Exception e) {
        HttpStatus status;

        if (e instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        final HashMap<String, String> valueMap = new HashMap<>();
        valueMap.put("message", e.getMessage());
        return new ResponseEntity<>(new JSONObject(valueMap), status);
    }
}
