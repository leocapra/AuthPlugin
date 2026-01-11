package br.com.otk.login.application.usecases;

import br.com.otk.login.application.session.SessionService;
import br.com.otk.login.domain.repository.PlayerRepository;
import br.com.otk.login.domain.valueobject.LoginResult;
import br.com.otk.login.domain.valueobject.PlayerStatus;

import java.util.UUID;

public record LoginUseCase(PlayerRepository repository, SessionService sessionService) {

    public LoginResult execute(UUID uuid, String password) {
        boolean matchPassword = repository.matchPassword(uuid, password);
        boolean existsAcount = repository.exists(uuid);

        if (!existsAcount) {
            return LoginResult.DOESNT_EXIST;
        }
        if (!matchPassword) {
            return LoginResult.PASSWORD_MISMATCH;
        }
        if (password.isEmpty()) {
            return LoginResult.EMPTY_PASSWORD;
        }

        repository.updateStatus(uuid, PlayerStatus.LOGADO);
        sessionService.login(uuid);
        return LoginResult.SUCCESS;
    }
}
