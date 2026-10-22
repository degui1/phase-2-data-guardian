package com.api_application.phase_2_data_guardian.infra.database.repository;

import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.infra.database.entity.MenuItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemJpaRepository extends JpaRepository<MenuItemJpaEntity, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);
}
