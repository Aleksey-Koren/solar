package io.solar.entity.modification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Data
@Table(name = "modification_types")
@NoArgsConstructor
@AllArgsConstructor
public class ModificationType {

    @Id
    private Long id;
    private String title;

    @OneToMany(mappedBy = "modificationType")
    private List<Modification> modifications;
}