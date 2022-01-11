package DTO.UserDTOS;

import entities.User;

public class UserDTO {

    private final String userName;

    public UserDTO(User user) {
        this.userName = user.getUserName();
    }

    public String getUserName() {
        return userName;
    }
}
