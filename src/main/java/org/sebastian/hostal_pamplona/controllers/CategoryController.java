package org.sebastian.hostal_pamplona.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastian.hostal_pamplona.common.helpers.ConvertsTransactions;
import org.sebastian.hostal_pamplona.common.utils.ApiResponseConsolidation;
import org.sebastian.hostal_pamplona.common.utils.CustomPagedResourcesAssembler;
import org.sebastian.hostal_pamplona.common.utils.ResponseWrapper;
import org.sebastian.hostal_pamplona.common.validations.BindingValidations;
import org.sebastian.hostal_pamplona.dtos.create.CreateCategoryDTO;
import org.sebastian.hostal_pamplona.persistence.entities.Category;
import org.sebastian.hostal_pamplona.services.ICategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Controlador de Categorías", description = "Operaciones relacionadas con las categorías de habitación")
public class CategoryController {

    private final ICategoryService categoryService;
    private final BindingValidations bindingValidations;
    private final ConvertsTransactions convertsTransactions;
    private final CustomPagedResourcesAssembler<Category> customPagedResourcesAssembler;

    @PostMapping("/create")
    @Operation(summary = "Crear una Temática", description = "Creación de una temática")
    public ResponseEntity<ApiResponseConsolidation<Object>> create(
            @Valid
            @RequestBody CreateCategoryDTO categoryRequest,
            BindingResult result
    ){

        //? ******************************
        //? Validamos primero los campos
        //? ******************************
        ResponseEntity<Object> validator = bindingValidations.validationBindings(result);
        Object responseValidator = validator.getBody();

        if( responseValidator == null ){

            //? *********************************
            //? Intentamos realizar el registro
            //? *********************************
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

            //? ****************************************************
            //? Estamos en este punto, el registro no fue correcto
            //? ****************************************************
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseConsolidation<>(
                        null,
                        new ApiResponseConsolidation.Meta(
                                newCategory.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

        }else{

            //? ****************************************************
            //? Devolvemos los errores por campos para mostrar
            //? ****************************************************
            Map<String, String> validationErrors = (Map<String, String>) responseValidator;
            List<Map<String, String>> errorsList = convertsTransactions.convertErrorsToList(validationErrors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseConsolidation<>(
                            errorsList,
                            new ApiResponseConsolidation.Meta(
                                    "Error en los campos proporcionados",
                                    HttpStatus.BAD_REQUEST.value(),
                                    LocalDateTime.now()
                            )
                    ));

        }

    }


    @GetMapping("/find-by-id/{id}")
    @Operation(summary = "Obtener categorías por ID", description = "Obtener una categoría dado el ID")
    public ResponseEntity<ApiResponseConsolidation<Category>> findById(@PathVariable String id){

        ResponseWrapper<Category> category;

        //Validamos que el ID que nos proporcionan por la URL sea válido
        try {
            Long categoryId = Long.parseLong(id);
            category = categoryService.findById(categoryId);
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseConsolidation<>(
                            null,
                            new ApiResponseConsolidation.Meta(
                                    "El ID proporcionado es inválido para obtener por ID.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        //Si es diferente de null implica que lo encontramos
        if( category.getData() != null ){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseConsolidation<>(
                            category.getData(),
                            new ApiResponseConsolidation.Meta(
                                    "Categoría obtenida por ID.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        //En caso contrario, algún null, ocurrió un error
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseConsolidation<>(
                        null,
                        new ApiResponseConsolidation.Meta(
                                category.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

}
