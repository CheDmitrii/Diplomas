package ru.system.authentication.config.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import ru.system.authentication.entity.User;
import ru.system.authentication.service.user.UserService;
import ru.system.library.exception.HttpResponseEntityException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutSuccessHandler implements LogoutHandler {

    private final String LOGOUT_TYPE_VALUE = "LOGOUT";
    private String POST_LOGOUT_URL;
    private final RedisCacheManager cacheManager;
    private final UserService userService;

//    @Override
//    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new HttpResponseEntityException(HttpStatus.INTERNAL_SERVER_ERROR, "Wrong data of user to logout");
        }
        // todo: change on repository
        userService.saveUserJournal(user, LOGOUT_TYPE_VALUE);

        Cache cache = cacheManager.getCache("user");
        if (cache != null) {
            cache.evict(user.getLogin());
        }

        log.error("User {} success logout", user.getLogin());
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
//        try {
//            response.sendRedirect(POST_LOGOUT_URL);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
