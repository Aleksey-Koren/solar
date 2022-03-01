package io.solar.entity.modification;

import io.solar.entity.objects.ObjectTypeDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "modifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Modification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "modification_type_id")
    private ModificationType modificationType;

    private Byte level;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "modification", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<ParameterModification> parameterModifications;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany()
    @JoinTable(
            name = "modification_otd",
            joinColumns = @JoinColumn(name = "modification_id"),
            inverseJoinColumns = @JoinColumn(name = "otd_id")
    )
    private List<ObjectTypeDescription> availableObjectTypeDescriptions;
}