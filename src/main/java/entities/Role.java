package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Plaul
 */
@Entity
@Table(name = "roles")
@NamedQuery(name = "Role.deleteAllRows", query = "delete from Role")
@NamedNativeQuery(name = "Role.resetAutoIncrement", query = "ALTER TABLE roles AUTO_INCREMENT = 1;")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_name", length = 20)
    private String roleName;

    @ManyToMany(mappedBy = "roleList")
    private List<User> userList;

    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
