package uk.ac.ebi.eva.server.persistence.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name ="USERS")
public class User {

    @Id
    @Column(length = 256)
    private String email;

    @Column(nullable = false, length = 256)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "users")
    private Set<Role> userRoles;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "users")
    private Set<Group> userGroups;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<Group> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<Group> userGroups) {
        this.userGroups = userGroups;
    }
}
