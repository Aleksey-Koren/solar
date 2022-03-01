package io.solar.entity.modification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "parameter_modifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterModification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ParameterType parameterType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modification_id")
    @ToString.Exclude
    private Modification modification;

    private Double modificationValue;
}