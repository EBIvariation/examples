package uk.ac.ebi.eva.server.persistence.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name ="GROUPS")
public class Group {

    @Id
    @Column(length = 256)
    private String groupId;

    @Column(length = 256, nullable = false)
    private String groupName;

    @Column(length = 2000, nullable = false)
    private String groupDescription;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "groups")
    private final Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="GROUP_USERS",
            joinColumns=@JoinColumn(name="GROUP_ID"),
            inverseJoinColumns=@JoinColumn(name="USER_ID"))
    private final Set<User> users;

    public Group() {
        roles = new HashSet<>();
        users = new HashSet<>();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public Set<Role> getRoles() {
        return roles;
    }

}
