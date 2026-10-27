package com.api_application.phase_2_data_guardian.infra.database.adapters;

import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.RestaurantPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.repository.RestaurantJpaRepository;
import com.api_application.phase_2_data_guardian.infra.database.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantPersistenceAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final RestaurantPersistenceMapper mapper;

    public RestaurantPersistenceAdapter(
            RestaurantJpaRepository repository,
            UserJpaRepository userRepository,
            RestaurantPersistenceMapper mapper
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        UserJpaEntity userEntity =
                this.userRepository.findById(restaurant.getOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Owner", restaurant.getOwnerId()
                        ));

        RestaurantJpaEntity entity = mapper.toJpaEntity(restaurant, userEntity);

        RestaurantJpaEntity savedEntity = repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Restaurant> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void delete(Restaurant restaurant) {
        repository.deleteById(restaurant.getId());
    }
}
