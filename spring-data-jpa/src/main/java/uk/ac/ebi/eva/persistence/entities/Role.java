package uk.ac.ebi.eva.persistence.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name ="ROLES")
public class Role {

    @Id
    @Column(length = 256)
    private String roleId;

    @Column(length = 2000)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="USER_ROLES",
            joinColumns=@JoinColumn(name="ROLE_ID"),
            inverseJoinColumns=@JoinColumn(name="USER_ID"))
    private final Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="GROUP_ROLES",
            joinColumns=@JoinColumn(name="ROLE_ID"),
            inverseJoinColumns=@JoinColumn(name="GROUP_ID"))
    private final Set<Group> groups;

    public Role() {
        users = new HashSet<>();
        groups = new HashSet<>();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Set<Group> getGroups() {
        return groups;
    }
}
