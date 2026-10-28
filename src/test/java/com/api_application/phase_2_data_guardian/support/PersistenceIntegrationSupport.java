package com.api_application.phase_2_data_guardian.support;

import com.api_application.phase_2_data_guardian.application.repository.MenuItemRepository;
import com.api_application.phase_2_data_guardian.application.repository.RestaurantRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserRepository;
import com.api_application.phase_2_data_guardian.application.repository.UserTypeRepository;
import com.api_application.phase_2_data_guardian.application.usecase.MenuItemUseCase;
import com.api_application.phase_2_data_guardian.application.usecase.RestaurantUseCase;
import com.api_application.phase_2_data_guardian.application.usecase.UserTypeUseCase;
import com.api_application.phase_2_data_guardian.application.usecase.UserUseCase;
import com.api_application.phase_2_data_guardian.domain.entity.Address;
import com.api_application.phase_2_data_guardian.domain.entity.MenuItem;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningHours;
import com.api_application.phase_2_data_guardian.domain.entity.OpeningPeriod;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import com.api_application.phase_2_data_guardian.domain.entity.UserType;
import com.api_application.phase_2_data_guardian.infra.configuration.MenuItemConfiguration;
import com.api_application.phase_2_data_guardian.infra.configuration.RestaurantConfiguration;
import com.api_application.phase_2_data_guardian.infra.configuration.UserConfiguration;
import com.api_application.phase_2_data_guardian.infra.configuration.UserTypeConfiguration;
import com.api_application.phase_2_data_guardian.infra.database.adapters.MenuItemPersistenceAdapter;
import com.api_application.phase_2_data_guardian.infra.database.adapters.RestaurantPersistenceAdapter;
import com.api_application.phase_2_data_guardian.infra.database.adapters.UserPersistenceAdapter;
import com.api_application.phase_2_data_guardian.infra.database.adapters.UserTypePersistenceAdapter;
import com.api_application.phase_2_data_guardian.infra.database.mapper.AddressPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.MenuItemPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.OpeningHoursPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.OpeningPeriodPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.RestaurantPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.UserPersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.mapper.UserTypePersistenceMapper;
import com.api_application.phase_2_data_guardian.infra.database.repository.RestaurantJpaRepository;
import com.api_application.phase_2_data_guardian.infra.database.repository.UserTypeJpaRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;

@org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase(replace = org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(showSql = false)
@ActiveProfiles(resolver = com.api_application.phase_2_data_guardian.support.TestDatabaseProfilesResolver.class)
@Import({UserTypePersistenceAdapter.class, UserPersistenceAdapter.class, RestaurantPersistenceAdapter.class,
        MenuItemPersistenceAdapter.class, UserTypePersistenceMapper.class, UserPersistenceMapper.class,
        RestaurantPersistenceMapper.class, MenuItemPersistenceMapper.class, AddressPersistenceMapper.class,
        OpeningHoursPersistenceMapper.class, OpeningPeriodPersistenceMapper.class, UserTypeConfiguration.class,
        UserConfiguration.class, RestaurantConfiguration.class, MenuItemConfiguration.class})
public abstract class PersistenceIntegrationSupport {
    @Autowired protected UserTypeRepository types;
    @Autowired protected UserRepository users;
    @Autowired protected RestaurantRepository restaurants;
    @Autowired protected MenuItemRepository items;
    @Autowired protected UserTypeUseCase typeService;
    @Autowired protected UserUseCase userService;
    @Autowired protected RestaurantUseCase restaurantService;
    @Autowired protected MenuItemUseCase menuService;
    @Autowired protected UserTypeJpaRepository typeJpa;
    @Autowired protected RestaurantJpaRepository restaurantJpa;
    @Autowired protected EntityManager em;
    protected User owner;
    protected UserType ownerType;
    protected UserType customerType;

    @BeforeEach protected void prepare() {
        ownerType = types.save(new UserType("RESTAURANT_OWNER"));
        customerType = types.save(new UserType("CUSTOMER"));
        owner = users.save(new User("Owner", "owner@example.com", "test-hash", ownerType.getId()));
        em.flush();
    }
    protected Restaurant restaurant(String name) {
        OpeningHours hours = new OpeningHours();
        hours.setMonday(List.of(new OpeningPeriod(LocalTime.of(8,0), LocalTime.of(18,0))));
        return restaurantService.create(new Restaurant(name,
                new Address("Street","10",null,"Center","City","SP","01001-000"),"Brazilian",hours,owner.getId()));
    }
    protected MenuItem item(Long restaurantId, String name) {
        return menuService.create(owner.getId(),
                new MenuItem(restaurantId,name,"Description",2590L,false,"/image.jpg"));
    }
    protected void reload() { em.flush(); em.clear(); }
}
