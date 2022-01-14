package DTO.UserDTOS;

import entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private String userName;
    private String address;
    private String phone;
    private String email;
    private String birthYear;
    private String gender;
    private List<UserDTO> users;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.userName = user.getUserName();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.birthYear = user.getBirthYear();
        this.gender = user.getGender();
    }

    public UserDTO(List<User> users) {
        if (users != null && !users.isEmpty()) {
            this.users = new ArrayList<>();
            for (User user : users) {
                this.users.add(new UserDTO(user));
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

}
