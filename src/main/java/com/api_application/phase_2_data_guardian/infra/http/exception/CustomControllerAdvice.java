package com.api_application.phase_2_data_guardian.infra.http.exception;

import com.api_application.phase_2_data_guardian.domain.exception.BusinessRuleException;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.domain.exception.ForbiddenOperationException;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(
            ResourceNotFoundException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );

        problem.setTitle("Resource not found");
        problem.setProperty("code", "RESOURCE_NOT_FOUND");

        return problem;
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbiddenOperationException(
            ForbiddenOperationException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                exception.getMessage()
        );

        problem.setTitle("Access denied");
        problem.setProperty("code", "FORBIDDEN_OPERATION");

        return problem;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRuleException(
            BusinessRuleException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );

        problem.setTitle("Business rule violation");
        problem.setProperty("code", "BUSINESS_RULE_VIOLATION");

        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(
            ConflictException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );

        problem.setTitle("Resource conflict");
        problem.setProperty("code", "RESOURCE_CONFLICT");

        return problem;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (firstMessage, secondMessage) -> firstMessage
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "One or more fields are invalid"
        );

        problem.setTitle("Validation failed");
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("errors", errors);

        return problem;
    }
}
