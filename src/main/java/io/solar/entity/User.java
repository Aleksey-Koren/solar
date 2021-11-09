package io.solar.entity;


import lombok.Data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;


@Data
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    @ManyToOne
    @JoinColumn(name = "planet")
    private Planet planet;
    private Instant hackBlock;
    private Integer hackAttempts;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //TODO decide how to realize when i know witch authorities we have
        return null;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }





//    @JsonIgnore
//    public Instant getHackBlock() {
//        return hackBlock;
//    }
//
//    public boolean can(String permission, Transaction transaction) {
//        return AuthController.userCan(this, permission, transaction);
//    }
//
//    @JsonIgnore
//    public String getPassword() {
//        return password;
//    }
//
//
//
//    @JsonProperty
//    public void setPassword(String password) {
//        this.password = password;
//    }

    //    private Map<String, Permission> permissions;

}
