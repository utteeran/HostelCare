
package com.example.hosteltracker.repository;

import com.example.hosteltracker.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
