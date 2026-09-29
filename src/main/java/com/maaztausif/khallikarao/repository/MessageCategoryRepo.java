package com.maaztausif.khallikarao.repository;

import com.maaztausif.khallikarao.entity.MessageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageCategoryRepo
        extends JpaRepository<MessageCategory, Long> {

    List<MessageCategory> findByActiveTrueOrderByNameAsc();
}