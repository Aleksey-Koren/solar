package io.solar.entity;

import io.solar.controller.AuthController;
import io.solar.utils.db.Transaction;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    private Long planet;
    private Instant hackBlock;
    private Integer hackAttempts;

//    private Map<String, Permission> permissions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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

}
