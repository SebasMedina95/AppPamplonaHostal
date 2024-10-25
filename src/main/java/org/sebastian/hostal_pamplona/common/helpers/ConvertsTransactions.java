package org.sebastian.hostal_pamplona.common.helpers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class ConvertsTransactions {

    public List<Map<String, String>> convertErrorsToList(Map<String, String> validationErrors) {

        List<Map<String, String>> errorsList = new ArrayList<>();

        validationErrors.forEach((field, message) -> {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("campo", field);
            errorMap.put("descripcion", message);
            errorsList.add(errorMap);
        });

        return errorsList;
    }

}
