package springboot.login.controllers;

import springboot.login.config.UserAuthenticationProvider;
import springboot.login.dtos.CredentialsDto;
import springboot.login.dtos.SignUpDto;
import springboot.login.dtos.UserDto;
import springboot.login.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

        private final UserService userService;
        private final UserAuthenticationProvider userAuthenticationProvider;

        @PostMapping("/login")
        public ResponseEntity<?> login(
                        @RequestBody @Valid CredentialsDto credentialsDto) {
                try {
                        UserDto userDto = userService
                                        .login(credentialsDto);
                        userDto.setToken(
                                        userAuthenticationProvider
                                                        .createToken(userDto));
                        return ResponseEntity.ok(userDto);
                } catch (BadCredentialsException ex) {
                        // Return 401 Unauthorized with a specific error message
                        return ResponseEntity.status(401)
                                        .body(ex.getMessage());
                }
        }

        @PostMapping("/register")
        public ResponseEntity<UserDto> register(
                        @RequestBody @Valid SignUpDto user) {
                UserDto createdUser = userService.register(user);
                createdUser.setToken(userAuthenticationProvider
                                .createToken(createdUser));
                return ResponseEntity.created(URI.create(
                                "/users/" + createdUser.getId()))
                                .body(createdUser);
        }

}
