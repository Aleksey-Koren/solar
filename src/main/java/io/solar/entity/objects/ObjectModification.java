package io.solar.entity.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "object_modification")
public class ObjectModification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ObjectTypeDescription item;

    @JoinColumn(name = "modification_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ObjectModificationType modification;
}
