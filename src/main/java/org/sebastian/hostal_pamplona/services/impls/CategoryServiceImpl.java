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

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ResponseWrapper<Category> create(CreateCategoryDTO category) {
        return null;
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
