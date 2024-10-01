package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Request;
import com.bezkoder.springjwt.models.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {


    boolean existsBySenderAndReceiver(Long sender, Long receiver);

    List<Request> findAllByReceiver(Long receiver);

    Request findBySenderAndReceiver(Long sender, Long receiver);
}
