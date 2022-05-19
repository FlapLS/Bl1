package com.example.bl_lab1.repositories;

import com.example.bl_lab1.model.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepo extends JpaRepository<ArticleEntity, Integer> {
    ArticleEntity findFirstByArticlename(String name);
}
