package com.api_application.phase_2_data_guardian.infra.database.repository;

import com.api_application.phase_2_data_guardian.infra.database.entity.MenuItemJpaEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemJpaRepository extends JpaRepository<MenuItemJpaEntity, Long> {
    Page<MenuItemJpaEntity> findByRestaurant_Id(Long restaurantId, Pageable pageable);
    Optional<MenuItemJpaEntity> findByIdAndRestaurant_Id(Long id, Long restaurantId);
    boolean existsByRestaurant_Id(Long restaurantId);
}
