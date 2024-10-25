package org.sebastian.hostal_pamplona.common.validations;

import lombok.extern.slf4j.Slf4j;
import org.sebastian.hostal_pamplona.common.utils.ApiResponseConsolidation;
import org.sebastian.hostal_pamplona.common.utils.ErrorsValidationsResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;

@Configuration
@Slf4j
public class BindingValidations {

    public ResponseEntity<ApiResponseConsolidation<Object>> validationBindings(BindingResult result){

        if(result.hasFieldErrors()){
            ErrorsValidationsResponse errors = new ErrorsValidationsResponse();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseConsolidation<>(
                            errors.validation(result),
                            new ApiResponseConsolidation.Meta(
                                    "Errores en los campos de creación",
                                    HttpStatus.BAD_REQUEST.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseConsolidation<>(
                        null,
                        new ApiResponseConsolidation.Meta(
                                "Los campos estan OK",
                                HttpStatus.OK.value(),
                                LocalDateTime.now()
                        )
                ));

    }

}
