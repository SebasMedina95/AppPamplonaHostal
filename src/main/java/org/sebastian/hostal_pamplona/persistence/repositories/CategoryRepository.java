package org.sebastian.hostal_pamplona.persistence.repositories;

import org.sebastian.hostal_pamplona.persistence.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query("SELECT c FROM Category c " +
            "WHERE c.status = true AND" +
            "(UPPER(c.name) LIKE UPPER(CONCAT('%', :search, '%')) OR " +
            "UPPER(c.description) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Category> findAll(
            @Param("search") String search,
            Pageable pageable
    );

}