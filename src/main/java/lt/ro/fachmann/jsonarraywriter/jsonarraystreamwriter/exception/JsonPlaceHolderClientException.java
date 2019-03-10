package lt.ro.fachmann.jsonarraywriter.jsonarraystreamwriter.exception;

public class JsonPlaceHolderClientException extends RuntimeException {
    public JsonPlaceHolderClientException() {
    }

    public JsonPlaceHolderClientException(String message) {
        super(message);
    }

    public JsonPlaceHolderClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonPlaceHolderClientException(Throwable cause) {
        super(cause);
    }
}
