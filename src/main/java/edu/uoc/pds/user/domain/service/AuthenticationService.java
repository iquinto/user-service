package edu.uoc.pds.user.domain.service;

import edu.uoc.pds.user.application.request.LoginRequest;
import edu.uoc.pds.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.CharBuffer;
import java.util.Base64;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public AuthenticatedUserResponse signIn(LoginRequest loginRequest) {
        User user = userService.findUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + loginRequest.getEmail()));

        if (passwordEncoder.matches(CharBuffer.wrap(loginRequest.getPassword()), user.getPassword())) {
            AuthenticatedUserResponse userResponse = AuthenticatedUserResponse.fromDomain(user)
            userResponse.setToken(createToken(user))
            return userResponse;
        }

        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }



    public UserDto validateToken(String token) {
        String login = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        Optional<BookstoreUser> userOptional = userRepository.findByLogin(login);

        if (userOptional.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        BookstoreUser user = userOptional.get();
        return userMapper.toUserDto(user, createToken(user));
    }

    private String createToken(BookstoreUser user) {
        Claims claims = Jwts.claims().setSubject(user.getLogin());

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hour

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

}
