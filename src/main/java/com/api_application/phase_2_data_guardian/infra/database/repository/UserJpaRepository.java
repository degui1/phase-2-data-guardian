package com.api_application.phase_2_data_guardian.infra.database.repository;

import com.api_application.phase_2_data_guardian.infra.database.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
}
