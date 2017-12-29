package com.gastocks.server.resources

import com.gastocks.server.models.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class ExceptionHandlerController {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ResponseBody
    static ErrorResponse requestHandlingNoHandlerFound() {
        new ErrorResponse("HTTP_404", "Not Found")
    }
}