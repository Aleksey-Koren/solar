package io.solar.entity;

import io.solar.controller.AuthController;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

@Getter
@Setter
public class User {
    private Long id;
    private String title;
    private String login;
    private String password;

    Map<String, Permission> permissions;

    public boolean can(String permission) {
        return AuthController.userCan(this, permission);
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
