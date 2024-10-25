package org.sebastian.hostal_pamplona.services;

import org.sebastian.hostal_pamplona.common.utils.ResponseWrapper;
import org.sebastian.hostal_pamplona.dtos.create.CreateCategoryDTO;
import org.sebastian.hostal_pamplona.dtos.update.UpdateCategoryDTO;
import org.sebastian.hostal_pamplona.persistence.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {

    ResponseWrapper<Category> create(CreateCategoryDTO category);
    Page<Category> findAll(String search, Pageable pageable);
    ResponseWrapper<Category> findById(Long id);
    ResponseWrapper<Object> update(Long id, UpdateCategoryDTO category);
    ResponseWrapper<Object> delete(Long id);

}
