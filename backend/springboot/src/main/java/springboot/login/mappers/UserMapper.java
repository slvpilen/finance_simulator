package springboot.login.mappers;

import springboot.login.dtos.SignUpDto;
import springboot.login.dtos.UserDto;
import springboot.login.entites.User;

import java.beans.JavaBean;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@JavaBean
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}
