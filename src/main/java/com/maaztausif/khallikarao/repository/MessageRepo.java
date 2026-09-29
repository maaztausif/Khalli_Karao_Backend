package com.maaztausif.khallikarao.repository;

import com.maaztausif.khallikarao.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message, Long> {

    Page<Message> findBySender_IdOrderByCreatedAtDescIdDesc(
            Long senderId,
            Pageable pageable
    );

    Optional<Message> findByIdAndSender_Id(
            Long id,
            Long senderId
    );
}