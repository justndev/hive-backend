package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Message;
import com.bezkoder.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Override
    List<Message> findAllById(Iterable<Long> longs);

    @Override
    <S extends Message> S save(S entity);

    @Override
    boolean existsById(Long aLong);

    List<Message> findAllBySenderAndReceiver(Long sender, Long receiver);
}
