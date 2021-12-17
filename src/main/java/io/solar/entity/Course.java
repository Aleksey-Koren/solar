package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "object_id")
    private BasicObject object;
    private Float x;
    private Float y;
    @Column(name = "acceleration_x")
    private Float accelerationX;
    @Column(name = "acceleration_y")
    private Float accelerationY;
    @OneToOne
    @JoinColumn(name = "next")
    private Course next;
    private Instant createdAt;
    private Instant expireAt;
}
