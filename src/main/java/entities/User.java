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

    public String getUserName() {
        return userName;
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

}
