package ru.kata.spring.boot_security.demo.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Data
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    @Override
    public String getAuthority() {
        return getName();
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {}
}
