package com.api_application.phase_2_data_guardian.security;

import com.api_application.phase_2_data_guardian.infra.security.adapters.PasswordHasherAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherAdapterTest {
    final PasswordHasherAdapter hasher=new PasswordHasherAdapter(new BCryptPasswordEncoder());
    @Test void hashIsNotPlaintextAndMatchesOriginalPassword() {
        String hash=hasher.hash("Strong@123");
        assertThat(hash).isNotEqualTo("Strong@123");
        assertThat(hasher.matches("Strong@123",hash)).isTrue();
    }
    @Test void wrongPasswordDoesNotMatch() {
        assertThat(hasher.matches("Wrong@123",hasher.hash("Strong@123"))).isFalse();
    }
    @Test void repeatedHashingUsesDifferentSaltsAndBothHashesMatch() {
        String first=hasher.hash("Strong@123");
        String second=hasher.hash("Strong@123");
        assertThat(first).isNotEqualTo(second);
        assertThat(hasher.matches("Strong@123",first)).isTrue();
        assertThat(hasher.matches("Strong@123",second)).isTrue();
    }
}
