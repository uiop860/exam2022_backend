package entities;

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQuery(name = "User.deleteAllRows", query = "delete from User")
@NamedNativeQuery(name = "User.resetAutoIncrement", query = "ALTER TABLE users AUTO_INCREMENT = 1;")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_name", nullable = false)
    @NotNull
    private String userName;


    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_pass")
    private String userPass;

    @Basic(optional = false)
    @NotNull
    @Column(name = "address")
    private String address;

    @Basic(optional = false)
    @NotNull
    @Column(name = "phone")
    private String phone;

    @Basic(optional = false)
    @NotNull
    @Column(name = "email")
    private String email;

    @Basic(optional = false)
    @NotNull
    @Column(name = "birth_year")
    private String birthYear;

    @Basic(optional = false)
    @NotNull
    @Column(name = "gender")
    private String gender;

    @JoinTable(name = "user_roles", joinColumns = {
            @JoinColumn(name = "user_name", referencedColumnName = "user_name")},
            inverseJoinColumns = { @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToMany
    private List<Role> roleList = new ArrayList<>();

    @JoinTable(name = "user_trips", joinColumns = {
            @JoinColumn(name = "user_name", referencedColumnName = "user_name")},
            inverseJoinColumns = { @JoinColumn(name = "trip_id", referencedColumnName = "id")})
    @ManyToMany()
    private List<Trip> trips = new ArrayList<>();

    public List<String> getRolesAsStrings() {
        if (roleList.isEmpty()) {
            return null;
        }
        List<String> rolesAsStrings = new ArrayList<>();
        roleList.forEach((role) -> {
            rolesAsStrings.add(role.getRoleName());
        });
        return rolesAsStrings;
    }

    public User() {
    }

    public boolean verifyPassword(String pw, String hashedPw) {
        return BCrypt.checkpw(pw, hashedPw);
    }

    public User(String userName, String userPass) {
        this.userName = userName;
        String salt = BCrypt.gensalt();
        this.userPass = BCrypt.hashpw(userPass, salt);
    }

    public User(String userName, String userPass, String address, String phone, String email, String birthYear, String gender) {
        this.userName = userName;
        String salt = BCrypt.gensalt();
        this.userPass = BCrypt.hashpw(userPass,salt);
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.birthYear = birthYear;
        this.gender = gender;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        String salt = BCrypt.gensalt();
        this.userPass = BCrypt.hashpw(userPass, salt);
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public void addRole(Role userRole) {
        roleList.add(userRole);
    }

    public void addTrip(Trip trip){
        this.trips.add(trip);
        trip.addUser(this);
    }

    public void removeTrip(Trip trip){
        if(this.trips != null && !this.trips.isEmpty()){
            this.trips.remove(trip);
            trip.removeUser(this);
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getGender() {
        return gender;
    }
}
