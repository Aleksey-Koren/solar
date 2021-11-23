package io.solar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "utilities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Utility {
    @Id
    private String utilKey;
    private String utilValue;
}
