package org.luismore.hlvsapi.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "entry_type")
public class EntryType {
    @Id
    @Column(name = "id", length = 4)
    private String id;

    @Column(name = "type")
    private String type;
}
