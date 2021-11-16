package io.solar.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class PermissionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @ManyToMany (mappedBy = "permissionTypes")
    private Set<User> users;

    public PermissionType () {

    }

    public PermissionType (Long id, String title) {
        this.id = id;
        this.title = title;
    }


    public String toString() {
        return "PermissionType(id=" + this.getId() + ", title=" + this.getTitle() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionType that = (PermissionType) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Set<User> getUsers() {
        return this.users;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}


