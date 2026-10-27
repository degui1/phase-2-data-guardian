package com.api_application.phase_2_data_guardian.application.usecase;

import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticatedUser;
import com.api_application.phase_2_data_guardian.application.model.auth.AuthenticationResult;
import com.api_application.phase_2_data_guardian.application.model.auth.LoginCommand;
import com.api_application.phase_2_data_guardian.application.model.auth.RegisterCommand;

public interface AuthUseCase {

    AuthenticationResult register(RegisterCommand command);

    AuthenticationResult login(LoginCommand command);

    AuthenticatedUser findAuthenticatedUser(Long userId);
}
