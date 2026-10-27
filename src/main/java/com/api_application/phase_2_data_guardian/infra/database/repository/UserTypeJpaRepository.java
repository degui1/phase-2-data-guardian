package com.api_application.phase_2_data_guardian.infra.database.repository;

import com.api_application.phase_2_data_guardian.infra.database.entity.UserTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTypeJpaRepository extends JpaRepository<UserTypeJpaEntity, Long> {

    Optional<UserTypeJpaEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
