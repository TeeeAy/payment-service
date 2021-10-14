package backend.template.handler;

import org.springframework.http.ResponseEntity;

public interface HystrixExceptionHandler<T> {

    boolean isAppropriateHandler(Throwable exception);

    ResponseEntity<String> handleException(T exception);

}
