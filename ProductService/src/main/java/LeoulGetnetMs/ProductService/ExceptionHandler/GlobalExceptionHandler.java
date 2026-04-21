package LeoulGetnetMs.ProductService.ExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleMethodNotValidException(MethodArgumentNotValidException ex){
       Map<String,String> errors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e->errors.put(e.getField(),e.getDefaultMessage()));
        /*Foreach substitutes stream.map*/
        return ResponseEntity.badRequest().body(errors); }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String,Object>> handleInsufficientBalanceException(InsufficientBalanceException ex){
        return buildErrorResponse(HttpStatus.FORBIDDEN,"Insufficient Stock Amount");
    }

    @ExceptionHandler(ProductNotFoundException.class)
   public ResponseEntity<Map<String,Object>> handleProductNotFoundException(ProductNotFoundException ex){
        return buildErrorResponse(HttpStatus.NOT_FOUND,"Product Not NotFound"); }


    private ResponseEntity<Map<String,Object>> buildErrorResponse(HttpStatus httpStatus, String message ){
        Map<String,Object> response=new HashMap<>();
        response.put("timestamp",LocalDateTime.now());
        response.put("status",httpStatus.value());
        response.put("message",message);
        return new ResponseEntity<>(response, httpStatus);  // Returns correct HTTP status
    }}