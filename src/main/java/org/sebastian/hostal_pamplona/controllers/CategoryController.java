package org.sebastian.hostal_pamplona.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastian.hostal_pamplona.common.utils.ApiResponseConsolidation;
import org.sebastian.hostal_pamplona.common.utils.CustomPagedResourcesAssembler;
import org.sebastian.hostal_pamplona.common.utils.ErrorsValidationsResponse;
import org.sebastian.hostal_pamplona.common.utils.ResponseWrapper;
import org.sebastian.hostal_pamplona.common.validations.BindingValidations;
import org.sebastian.hostal_pamplona.dtos.create.CreateCategoryDTO;
import org.sebastian.hostal_pamplona.persistence.entities.Category;
import org.sebastian.hostal_pamplona.services.ICategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/categories")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Controlador de Categorías", description = "Operaciones relacionadas con las categorías de habitación")
public class CategoryController {

    private final ICategoryService categoryService;
    private final BindingValidations bindingValidations;
    private final CustomPagedResourcesAssembler<Category> customPagedResourcesAssembler;

    @PostMapping("/create")
    @Operation(summary = "Crear una Temática", description = "Creación de una temática")
    public ResponseEntity<ApiResponseConsolidation<Object>> create(
            @Valid
            @RequestBody CreateCategoryDTO categoryRequest,
            BindingResult result
    ){

        ResponseEntity<ApiResponseConsolidation<Object>> validator = bindingValidations.validationBindings(result);
        if( validator.getStatusCode().equals(HttpStatus.OK) ){

            //? Intentamos realizar el registro
            ResponseWrapper<Category> newCategory = categoryService.create(categoryRequest);

            //? Si no ocurre algún error, entonces registramos :)
            if( newCategory.getData() != null ){
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponseConsolidation<>(
                                newCategory.getData(),
                                new ApiResponseConsolidation.Meta(
                                        "Categoría Registrada Correctamente.",
                                        HttpStatus.CREATED.value(),
                                        LocalDateTime.now()
                                )
                        ));
            }

            //? Estamos en este punto, el registro no fue correcto
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseConsolidation<>(
                            null,
                            new ApiResponseConsolidation.Meta(
                                    newCategory.getErrorMessage(),
                                    HttpStatus.BAD_REQUEST.value(),
                                    LocalDateTime.now()
                            )
                    ));

        }

        return null;

    }

}
