package io.solar.specification.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
public class UserFilter {

    private String login;
    private String title;
    private Integer notInRoom;
}
