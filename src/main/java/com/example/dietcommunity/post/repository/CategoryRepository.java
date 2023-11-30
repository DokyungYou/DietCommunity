package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.entity.Category;
import com.example.dietcommunity.post.type.CategoryType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
 Optional<Category> findByIdAndCategoryType(long categoryId, CategoryType type);

 boolean existsByIdAndCategoryType(long categoryId, CategoryType type);
}
