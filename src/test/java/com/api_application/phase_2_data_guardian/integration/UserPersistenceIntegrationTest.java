package com.api_application.phase_2_data_guardian.integration;

import com.api_application.phase_2_data_guardian.application.model.user.UpdateUserCommand;
import com.api_application.phase_2_data_guardian.domain.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.api_application.phase_2_data_guardian.domain.entity.Restaurant;
import com.api_application.phase_2_data_guardian.domain.exception.ConflictException;
import com.api_application.phase_2_data_guardian.support.PersistenceIntegrationSupport;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class UserPersistenceIntegrationTest extends PersistenceIntegrationSupport {


    @Test void selfServiceTypeChangeCannotInvalidateRestaurantOwnership() {
        Restaurant restaurant = restaurant("Owned");
        assertThat(restaurants.existsByOwnerId(owner.getId())).isTrue();
        assertThatThrownBy(() -> userService.assignUserType(owner.getId(),owner.getId(),customerType.getId()))
                .isInstanceOf(ConflictException.class);
        restaurantService.delete(owner.getId(),restaurant.getId());
        reload();
        userService.assignUserType(owner.getId(),owner.getId(),customerType.getId());
        reload();
        assertThat(userService.findUserType(owner.getId()).getName()).isEqualTo("CUSTOMER");
    }
    @Test void profileUpdateIsPersistedWithoutChangingPasswordOrType() {
        Long id = owner.getId();
        reload();
        var createdAt = userService.findById(id,id).getCreatedAt();
        userService.update(id,id,new UpdateUserCommand("  Updated owner  "," UPDATED@EXAMPLE.COM "));
        reload();
        User result = userService.findById(id,id);
        assertThat(result.getName()).isEqualTo("Updated owner");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getUserTypeId()).isEqualTo(ownerType.getId());
        assertThat(result.getPassword()).isEqualTo("test-hash");
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test void anotherUsersEmailCannotBeTaken() {
        User another = users.save(new User("Another","taken@example.com","test-hash",customerType.getId()));
        reload();
        assertThatThrownBy(() -> userService.update(owner.getId(),owner.getId(),new UpdateUserCommand("Changed","TAKEN@example.com")))
                .isInstanceOf(ConflictException.class);
        reload();
        assertThat(users.findById(owner.getId()).orElseThrow().getEmail()).isEqualTo("owner@example.com");
        assertThat(users.findById(another.getId()).orElseThrow().getEmail()).isEqualTo("taken@example.com");
    }

    @Test void userDirectoryPaginatesAndSortsByName() {
        users.save(new User("Alpha","alpha@example.com","test-hash",customerType.getId()));
        users.save(new User("Zeta","zeta@example.com","test-hash",customerType.getId()));
        reload();
        var page = userService.findAll(PageRequest.of(1,1,Sort.by("name")));
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).extracting(User::getName).containsExactly("Owner");
    }

    @Test void accountWithNoRestaurantsCanBeDeletedWithoutDeletingItsType() {
        Long id = owner.getId();
        Long typeId = ownerType.getId();
        userService.delete(id,id);
        reload();
        assertThat(users.findById(id)).isEmpty();
        assertThat(types.findById(typeId)).isPresent();
    }

    @Test void accountDeletionIsBlockedWhileItOwnsRestaurants() {
        restaurant("Owned");
        reload();
        assertThatThrownBy(() -> userService.delete(owner.getId(),owner.getId())).isInstanceOf(ConflictException.class);
        assertThat(users.findById(owner.getId())).isPresent();
    }

}
