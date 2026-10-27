package com.api_application.phase_2_data_guardian.infra.database.adapters;

import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.exception.ResourceNotFoundException;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserTypeJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.UserPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.repository.UserJpaRepository;
import com.api_application.phase_2_data_guardian.infra.database.repository.UserTypeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository repository;
    private final UserTypeJpaRepository userTypeJpaRepository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(
            UserJpaRepository repository,
            UserTypeJpaRepository userTypeJpaRepository,
            UserPersistenceMapper mapper
    ) {
        this.repository = repository;
        this.userTypeJpaRepository = userTypeJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserTypeJpaEntity userType =
                userTypeJpaRepository.findById(user.getUserTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User type", user.getUserTypeId()
                        ));

        UserJpaEntity entity = mapper.toJpaEntity(user, userType);
        UserJpaEntity savedEntity = this.repository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return repository.findByEmailIgnoreCase(email).map(mapper::toDomain);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void delete(User user) {
        repository.deleteById(user.getId());
    }
}
