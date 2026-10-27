package com.api_application.phase_2_data_guardian.application.security;

import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticatedUser;
import com.api_application.phase_2_data_guardian.application.model.auth.TokenData;

public interface TokenProvider {

    TokenData generate(AuthenticatedUser user);
}
