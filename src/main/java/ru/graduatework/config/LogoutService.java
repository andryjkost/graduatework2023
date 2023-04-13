package ru.graduatework.config;

//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.stereotype.Service;
//import ru.graduatework.services.TokenService;

//@Service
//@RequiredArgsConstructor
//public class LogoutService implements LogoutHandler {
//
//    private final JwtService jwtService;
//    private final TokenService tokenService;
//
//    @Override
//    public void logout(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Authentication authentication
//    ) {
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        jwt = authHeader.substring(7);
//        final String userEmail;
//        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
//            return;
//        }
//        userEmail = jwtService.extractUsername(jwt);
//        var storedToken = tokenService.getByToken(jwt);
//        if (storedToken != null) {
//            storedToken.setExpired(true);
//            storedToken.setRevoked(true);
//            tokenService.save(storedToken);
//            SecurityContextHolder.clearContext();
//        }
//    }
//}