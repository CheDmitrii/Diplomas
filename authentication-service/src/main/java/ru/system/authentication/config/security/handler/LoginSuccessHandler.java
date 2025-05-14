package ru.system.authentication.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.system.authentication.entity.User;
import ru.system.authentication.service.user.UserService;
import ru.system.library.exception.HttpResponseEntityException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final String LOGIN_TYPE_VALUE = "LOGIN";
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        var principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Wrong data of user");
        }


        userService.saveUserJournal(user, LOGIN_TYPE_VALUE); // todo change on repository
        log.info("User {} success login", user.getLogin());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
