package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Message;
import com.bezkoder.springjwt.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository  extends JpaRepository<Post, Long> {

    @Override
    List<Post> findAll();


}
