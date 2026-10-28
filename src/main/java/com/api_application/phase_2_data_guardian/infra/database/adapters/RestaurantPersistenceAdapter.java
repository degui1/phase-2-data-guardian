package com.api_application.phase_2_data_guardian.infra.database.adapters;

import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.infra.database.entity.RestaurantJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.RestaurantPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.repository.RestaurantJpaRepository;
import com.api_application.phase_2_data_guardian.infra.database.repository.UserJpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Restaurant save(Restaurant restaurant) {
        UserJpaEntity userEntity =
                this.userRepository.findById(restaurant.getOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Owner", restaurant.getOwnerId()
                        ));

        RestaurantJpaEntity entity = mapper.toJpaEntity(restaurant, userEntity);

        if (restaurant.getId() != null) {
            RestaurantJpaEntity existing = repository.findById(restaurant.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant", restaurant.getId()));
            // Keep the identity of unchanged periods: inserting replacements before orphan deletion
            // would violate the unique constraint on restaurant, day, opening and closing times.
            for (var period : entity.getOpeningPeriods()) {
                existing.getOpeningPeriods().stream()
                        .filter(saved -> saved.getDayOfWeek() == period.getDayOfWeek()
                                && saved.getOpensAt().equals(period.getOpensAt())
                                && saved.getClosesAt().equals(period.getClosesAt()))
                        .findFirst()
                        .ifPresent(saved -> period.setId(saved.getId()));
            }
        }

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
    @Override
    public boolean existsByOwnerId(Long ownerId) {
        return repository.existsByOwner_Id(ownerId);
    }
}
