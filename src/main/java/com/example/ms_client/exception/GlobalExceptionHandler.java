package com.example.ms_client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFound(ClientNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Client not found", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(InvalidClientDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidClientData(InvalidClientDataException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Invalid client data", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(ClientNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleClientNotActive(ClientNotActiveException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Client is not active", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY); // 422
    }

    // Обработчик для исключения, когда изменение статуса клиента не разрешено
    @ExceptionHandler(ClientStatusChangeNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleClientStatusChangeNotAllowed(ClientStatusChangeNotAllowedException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Client status change not allowed", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY); // 422
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("errors", errorMessages); // как ожидается в тестах

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "Invalid UUID format: " + ex.getValue();
        ErrorResponse errorResponse = new ErrorResponse("Bad Request", errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeParseException(DateTimeParseException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("errors", List.of("createDateTime: Invalid date format")); // или другое имя поля, если ошибка по другому полю
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

}
