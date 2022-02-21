package io.solar.entity.modification;

import io.solar.entity.objects.ObjectTypeDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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

    @OneToMany(mappedBy = "modification")
    private List<ParameterModification> parameterModifications;

    @ManyToMany(mappedBy = "availableModifications")
    private List<ObjectTypeDescription> availableObjectTypeDescriptions;
}