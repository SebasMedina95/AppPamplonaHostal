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
    public Page<Category> findAll(String search, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseWrapper<Category> findById(Long id) {
        return null;
    }

    @Override
    public ResponseWrapper<Object> update(Long id, UpdateCategoryDTO category) {
        return null;
    }

    @Override
    public ResponseWrapper<Object> delete(Long id) {
        return null;
    }
}
