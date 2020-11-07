package de.sandkastenliga.resultserver.rest;

import de.sandkastenliga.resultserver.rest.dtos.ErrorDto;
import de.sandkastenliga.resultserver.services.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class ExceptionController {

    private static final Log log = LogFactory.getLog(ExceptionController.class);
    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorDto> handleServiceException(ServiceException se) {
        log.error(se);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(messageSource.getMessage(se.getMessage(), se.getArgs(), Locale.GERMAN));
        return new ResponseEntity<ErrorDto>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleServiceException(Throwable t) {
        log.error(t);
        return new ResponseEntity<String>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
