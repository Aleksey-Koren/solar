package io.solar.entity;

import io.solar.controller.AuthController;
import io.solar.utils.db.Transaction;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public class User {
    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    private Long planet;
    private Instant hackBlock;
    private Integer hackAttempts;

    @JsonIgnore
    public Instant getHackBlock() {
        return hackBlock;
    }

    Map<String, Permission> permissions;

    public boolean can(String permission, Transaction transaction) {
        return AuthController.userCan(this, permission, transaction);
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

}
