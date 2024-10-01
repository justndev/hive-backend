package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Chat;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    boolean existsBySenderAndReceiver(Long sender, Long receiver);

    Optional<Chat> findBySenderAndReceiver(Long sender, Long receiver);

    List<Chat> findBySender(Long user);

    List<Chat> findByReceiver(Long user);
}
