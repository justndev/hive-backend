package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Contact;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByReceiver(Long user);

    List<Contact> findAllBySender(Long user);

    List<Contact> findByReceiver(Long user);

    List<Contact> findBySender(Long user);

    boolean existsBySenderAndReceiver(Long user1, Long user2);
}
