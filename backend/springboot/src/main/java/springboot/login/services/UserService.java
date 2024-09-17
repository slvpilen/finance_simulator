package springboot.login.services;

import springboot.login.dtos.CredentialsDto;
import springboot.login.dtos.SignUpDto;
import springboot.login.dtos.UserDto;
import springboot.login.entites.User;
import springboot.login.exceptions.AppException;
import springboot.login.mappers.UserMapper;
import springboot.login.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        private final UserMapper userMapper;

        public UserDto login(CredentialsDto credentialsDto) {
                User user = userRepository
                                .findByLogin(credentialsDto
                                                .login())
                                .orElseThrow(() -> new AppException(
                                                "Unknown user",
                                                HttpStatus.NOT_FOUND));

                if (passwordEncoder.matches(
                                CharBuffer.wrap(credentialsDto
                                                .password()),
                                user.getPassword())) {
                        return userMapper.toUserDto(user);
                }
                throw new AppException("Invalid password",
                                HttpStatus.BAD_REQUEST);
        }

        public UserDto register(SignUpDto userDto) {
                Optional<User> optionalUser = userRepository
                                .findByLogin(userDto.login());

                if (optionalUser.isPresent()) {
                        throw new AppException(
                                        "Login already exists",
                                        HttpStatus.BAD_REQUEST);
                }

                User user = userMapper.signUpToUser(userDto);
                user.setPassword(passwordEncoder
                                .encode(CharBuffer.wrap(userDto
                                                .password())));

                User savedUser = userRepository.save(user);

                return userMapper.toUserDto(savedUser);
        }

        public UserDto findByLogin(String login) {
                User user = userRepository.findByLogin(login)
                                .orElseThrow(() -> new AppException(
                                                "Unknown user",
                                                HttpStatus.NOT_FOUND));
                return userMapper.toUserDto(user);
        }

}
