package com.api_application.phase_2_data_guardian.infra.database.adapters;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.infra.database.entity.MenuItemJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.MenuItemPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.repository.MenuItemJpaRepository;
import com.api_application.phase_2_data_guardian.infra.database.repository.RestaurantJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MenuItemPersistenceAdapter implements MenuItemRepository {

    private final MenuItemJpaRepository repository;
    private final MenuItemPersistenceMapper mapper;
    private final RestaurantJpaRepository restaurantRepository;

    public MenuItemPersistenceAdapter(
            MenuItemJpaRepository repository,
            RestaurantJpaRepository restaurantRepository,
            MenuItemPersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        RestaurantJpaEntity restaurantJpa =
                restaurantRepository.findById(menuItem.getRestaurantId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Restaurant", menuItem.getRestaurantId()
                        ));

        MenuItemJpaEntity entity = mapper.toJpaEntity(menuItem, restaurantJpa);

        MenuItemJpaEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<MenuItem> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void delete(MenuItem menuItem) {
        repository.deleteById(menuItem.getId());
    }

    @Override
    public Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable) {
        return repository
                .findByRestaurant_Id(restaurantId, pageable)
                .map(mapper::toDomain);
    }
}
