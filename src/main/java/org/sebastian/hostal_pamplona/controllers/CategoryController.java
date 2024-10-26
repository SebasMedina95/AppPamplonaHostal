package org.sebastian.hostal_pamplona.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastian.hostal_pamplona.common.helpers.ConvertsTransactions;
import org.sebastian.hostal_pamplona.common.paginations.PaginationDto;
import org.sebastian.hostal_pamplona.common.utils.ApiResponseConsolidation;
import org.sebastian.hostal_pamplona.common.utils.CustomPagedResourcesAssembler;
import org.sebastian.hostal_pamplona.common.utils.ResponseWrapper;
import org.sebastian.hostal_pamplona.common.validations.BindingValidations;
import org.sebastian.hostal_pamplona.dtos.create.CreateCategoryDTO;
import org.sebastian.hostal_pamplona.dtos.update.UpdateCategoryDTO;
import org.sebastian.hostal_pamplona.persistence.entities.Category;
import org.sebastian.hostal_pamplona.services.ICategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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

    @GetMapping("/find-all")
    @Operation(summary = "Obtener todas las categorías", description = "Obtener todas las categorías con paginación y también aplicando filtros")
    public ResponseEntity<ApiResponseConsolidation<Object>> findAll(
            @Valid
            @RequestBody PaginationDto paginationDto,
            BindingResult result
    ){

        //? ******************************
        //? Validamos primero los campos
        //? ******************************
        ResponseEntity<Object> validator = bindingValidations.validationBindings(result);
        Object responseValidator = validator.getBody();

        if( responseValidator == null ){

            if (paginationDto.getPage() < 1) paginationDto.setPage(1); //Para controlar la página 0, y que la paginación arranque en 1.

            Sort.Direction direction = paginationDto.getOrder().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(paginationDto.getPage() - 1, paginationDto.getSize(), Sort.by(direction, paginationDto.getSort())); //Generando el esquema de paginación para aplicar y ordenamiento
            Page<Category> categories = categoryService.findAll(paginationDto.getSearch(), pageable); //Aplicando la paginación JPA -> Incorporo el buscador
            UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri(); //Para la obtención de la URL

            PagedModel<Category> pagedModel = customPagedResourcesAssembler.toModel(categories, uriBuilder);

            return ResponseEntity.ok(new ApiResponseConsolidation<>(
                    pagedModel,
                    new ApiResponseConsolidation.Meta(
                            "Listado de categorías.",
                            HttpStatus.OK.value(),
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

    @PatchMapping("update-by-id/{id}")
    @Operation(summary = "Actualizar una categoría", description = "Actualizar una categoría dado el ID")
    public ResponseEntity<ApiResponseConsolidation<Object>> update(
            @Valid
            @RequestBody UpdateCategoryDTO updateCategoryDTO,
            BindingResult result,
            @PathVariable String id
    ){

        //? ******************************
        //? Validamos primero los campos
        //? ******************************
        ResponseEntity<Object> validator = bindingValidations.validationBindings(result);
        Object responseValidator = validator.getBody();

        if( responseValidator == null){

            ResponseWrapper<Object> categoryUpdate;

            try {
                Long categoryId = Long.parseLong(id);
                categoryUpdate = categoryService.update(categoryId, updateCategoryDTO);
            }catch (NumberFormatException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseConsolidation<>(
                                null,
                                new ApiResponseConsolidation.Meta(
                                        "El ID proporcionado es inválido para actualizar.",
                                        HttpStatus.OK.value(),
                                        LocalDateTime.now()
                                )
                        ));
            }

            if( categoryUpdate.getData() != null ){
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponseConsolidation<>(
                                categoryUpdate.getData(),
                                new ApiResponseConsolidation.Meta(
                                        "Categoría Actualizada Correctamente.",
                                        HttpStatus.OK.value(),
                                        LocalDateTime.now()
                                )
                        ));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseConsolidation<>(
                            null,
                            new ApiResponseConsolidation.Meta(
                                    categoryUpdate.getErrorMessage(),
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

    @DeleteMapping("/delete-by-id/{id}")
    @Operation(summary = "Eliminar una categoría", description = "Eliminar una categoría pero de manera lógica")
    public ResponseEntity<ApiResponseConsolidation<Object>> delete(
            @PathVariable String id
    ){

        ResponseWrapper<Object> categoryUpdate;

        try {
            Long categoryId = Long.parseLong(id);
            categoryUpdate = categoryService.delete(categoryId);
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseConsolidation<>(
                            null,
                            new ApiResponseConsolidation.Meta(
                                    "El ID proporcionado es inválido.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        if( categoryUpdate.getData() != null ){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseConsolidation<>(
                            categoryUpdate.getData(),
                            new ApiResponseConsolidation.Meta(
                                    "Categoría Eliminada Correctamente.",
                                    HttpStatus.OK.value(),
                                    LocalDateTime.now()
                            )
                    ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseConsolidation<>(
                        null,
                        new ApiResponseConsolidation.Meta(
                                categoryUpdate.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value(),
                                LocalDateTime.now()
                        )
                ));

    }

}
