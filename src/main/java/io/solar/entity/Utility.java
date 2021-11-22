package io.solar.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "utilities")
@Data
public class Utility {
    @Id
    private String utilKey;
    private String utilValue;
}
