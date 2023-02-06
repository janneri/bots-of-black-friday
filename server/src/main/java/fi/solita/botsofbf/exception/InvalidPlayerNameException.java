package fi.solita.botsofbf.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid player name")
public class InvalidPlayerNameException extends RuntimeException {
    public InvalidPlayerNameException(String msg) {
        super(msg);
    }
}
