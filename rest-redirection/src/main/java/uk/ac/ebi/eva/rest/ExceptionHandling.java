package uk.ac.ebi.eva.rest;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by jorizci on 03/11/16.
 */

/**
 * This is used to attach this advice to all the classes with this annotation, it can be done
 * by base package, class or annotation. In this example I opted for the annotation for practice
 * purpouses only.
 */
@ControllerAdvice(annotations = {RestBaseExceptionHandling.class})
public class ExceptionHandling {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors versionNotSupportedExceptionHandler(MethodArgumentNotValidException exception) {
        return new VndErrors("error", exception.getMessage());
    }

}
