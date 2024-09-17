package springboot.login.dtos;

public record CredentialsDto(String login, char[] password) {
}