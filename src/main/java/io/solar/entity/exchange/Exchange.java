package io.solar.entity.exchange;

import io.solar.entity.User;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Data
@Table(name = "exchanges")
public class Exchange {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "first_user_id")
    private User firstUser;

    @OneToOne
    @JoinColumn(name ="second_user_id")
    private User secondUser;

    private Boolean firstAccepted;
    private Boolean secondAccepted;
    private Instant startTime;
}
