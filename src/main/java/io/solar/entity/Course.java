package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    private BasicObject object;

    @Column(name = "duration")
    private Long time;

    @Column(name = "acceleration_x")
    private Double accelerationX;

    @Column(name = "acceleration_y")
    private Double accelerationY;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous")
    private Course previous;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next")
    private Course next;

    private Instant createdAt;
    private Long expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet")
    private Planet planet;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    public boolean hasNext() {
        return this.next != null;
    }

    public boolean hasPrevious() {
        return  this.previous != null;
    }
}