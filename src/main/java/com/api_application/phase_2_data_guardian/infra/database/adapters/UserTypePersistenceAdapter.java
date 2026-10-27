package com.api_application.phase_2_data_guardian.infra.database.adapters;

import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.database.entity.UserTypeJpaEntity;
import com.api_application.phase_2_data_guardian.infra.database.mapper.UserTypePersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.repository.UserTypeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserTypePersistenceAdapter implements UserTypeRepository {
    private final UserTypeJpaRepository repository;
    private final UserTypePersistenceMapper mapper;

    public UserTypePersistenceAdapter(UserTypeJpaRepository repository, UserTypePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserType save(UserType userType) {
        UserTypeJpaEntity savedEntity = repository.save(mapper.toJpaEntity(userType));
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserType> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<UserType> findByNameIgnoreCase(String name) {
        return repository.findByNameIgnoreCase(name).map(mapper::toDomain);
    }

    @Override
    public Page<UserType> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return repository.existsByNameIgnoreCase(name);
    }

    @Override
    public void delete(UserType userType) {
        repository.deleteById(userType.getId());
    }
}
