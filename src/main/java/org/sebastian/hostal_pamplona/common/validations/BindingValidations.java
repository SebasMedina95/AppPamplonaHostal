package org.sebastian.hostal_pamplona.common.validations;

import lombok.extern.slf4j.Slf4j;
import org.sebastian.hostal_pamplona.common.utils.ErrorsValidationsResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@Configuration
@Slf4j
public class BindingValidations {

    public ResponseEntity<Object> validationBindings(BindingResult result){

        if(result.hasFieldErrors()){

            log.error("Errores en los campos de entrada");
            ErrorsValidationsResponse errors = new ErrorsValidationsResponse();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.validation(result));

        }

        return ResponseEntity.status(HttpStatus.OK).body(null);

    }

}
