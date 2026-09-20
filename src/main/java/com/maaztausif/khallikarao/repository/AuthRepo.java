package com.maaztausif.khallikarao.repository;

import com.maaztausif.khallikarao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AuthRepo extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
