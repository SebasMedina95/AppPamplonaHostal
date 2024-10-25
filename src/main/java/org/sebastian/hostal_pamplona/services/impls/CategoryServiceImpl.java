package org.sebastian.hostal_pamplona.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sebastian.hostal_pamplona.common.utils.ResponseWrapper;
import org.sebastian.hostal_pamplona.dtos.create.CreateCategoryDTO;
import org.sebastian.hostal_pamplona.dtos.update.UpdateCategoryDTO;
import org.sebastian.hostal_pamplona.persistence.entities.Category;
import org.sebastian.hostal_pamplona.persistence.repositories.CategoryRepository;
import org.sebastian.hostal_pamplona.services.ICategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private static String dummiesUser = "usuario123";
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ResponseWrapper<Category> create(CreateCategoryDTO category) {

        //? *****************************************
        //? Validemos que no se repita la categoría
        //? *****************************************
        String categoryName = category.getName().trim().toUpperCase();
        Optional<Category> getThematicOptional = categoryRepository.getCategoryByName(categoryName);
        if( getThematicOptional.isPresent() )
            return new ResponseWrapper<>(null, "El nombre de la categoría ya se encuentra registrado");

        //? *********************************
        //? Agregamos primero la categoría
        //? *********************************
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory.setDescription(category.getDescription());
        newCategory.setPopulate(0);
        newCategory.setStatus(true);
        newCategory.setUserCreated(dummiesUser); //! Ajustar cuando se implemente Security
        newCategory.setDateCreated(new Date()); //! Ajustar cuando se implemente Security
        newCategory.setUserUpdated(dummiesUser); //! Ajustar cuando se implemente Security
        newCategory.setDateUpdated(new Date()); //! Ajustar cuando se implemente Security

        Category saveCategory = categoryRepository.save(newCategory);
        return new ResponseWrapper<>(saveCategory,"Categoría guardada correctamente");

    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> findAll(String search, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseWrapper<Category> findById(Long id) {

        try{

            Optional<Category> categoryOptional = categoryRepository.findById(id);
            if( categoryOptional.isPresent() ){
                Category category = categoryOptional.orElseThrow();
                return new ResponseWrapper<>(category, "Categoría encontrada por ID correctamente");
            }

            return new ResponseWrapper<>(null, "La categoría no pudo ser encontrado por el ID " + id);

        }catch (Exception err){

            log.error("Ocurrió un error al intentar obtener la categoría por ID {}, detalles: ", id, err);
            return new ResponseWrapper<>(null, "La categoría no pudo ser encontrado por el ID");

        }

    }

    @Override
    @Transactional
    public ResponseWrapper<Object> update(Long id, UpdateCategoryDTO category) {
        return null;
    }

    @Override
    @Transactional
    public ResponseWrapper<Object> delete(Long id) {
        return null;
    }
}
