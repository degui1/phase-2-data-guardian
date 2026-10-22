package com.api_application.phase_2_data_guardian.infra.database.repository;

import com.api_application.phase_2_data_guardian.infra.database.entity.UserTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeJpaRepository extends JpaRepository<UserTypeJpaEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
}
