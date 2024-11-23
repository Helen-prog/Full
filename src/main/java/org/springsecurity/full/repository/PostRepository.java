package org.springsecurity.full.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springsecurity.full.entity.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByCategory(String category);

    List<Post> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);

    Page<Post> findByCategory(Pageable pageable, String category);

    Page<Post> findAllBy(Pageable pageable);
}
