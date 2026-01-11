package br.com.otk.login.application.usecases;

import br.com.otk.login.application.session.SessionService;
import br.com.otk.login.domain.model.PlayerAccount;
import br.com.otk.login.domain.repository.PlayerRepository;
import br.com.otk.login.domain.valueobject.PlayerStatus;
import br.com.otk.login.domain.valueobject.RegisterResult;

import java.util.UUID;

public record RegisterUseCase(
        PlayerRepository repository,
        SessionService sessionService
) {

    public RegisterResult execute(
            UUID uuid,
            String username,
            String password,
            String confirmPassword
    ) {

        if (password == null || password.isBlank()) {
            return RegisterResult.EMPTY_PASSWORD;
        }

        if (password.length() < 8){
            return RegisterResult.INVALID_PASSWORD;
        }

        if (!password.equals(confirmPassword)) {
            return RegisterResult.PASSWORD_MISMATCH;
        }

        if (repository.exists(uuid)) {
            return RegisterResult.ALREADY_EXISTS;
        }


        PlayerAccount account =
                new PlayerAccount(uuid, username, password, PlayerStatus.LOGADO);

        repository.save(account);
        sessionService.login(uuid);

        return RegisterResult.SUCCESS;
    }
}
