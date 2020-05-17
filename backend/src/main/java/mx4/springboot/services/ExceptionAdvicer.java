/*
 * 2020.04.02  -- Created
 * 2020.04.10  -- Added invalid data handling
 */
package mx4.springboot.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Ilamah, Osho
 */
@ControllerAdvice
public class ExceptionAdvicer {

    @ResponseBody
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String itemNotFoundHandler(ItemNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String invalidDataFoundHandler(InvalidDataException ex) {
        return ex.getMessage();
    }
}
