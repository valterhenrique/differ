package diff;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid base64 content") // 400
class InvalidBase64Exception extends RuntimeException {

    public InvalidBase64Exception() {
        super("Invalid base64 content");
    }
}
