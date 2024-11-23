package org.springsecurity.full.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springsecurity.full.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    public Boolean existsByName(String name);
}
