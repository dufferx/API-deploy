package org.luismore.hlvsapi.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "states")
public class State {
    @Id
    @Column(name = "id", length = 4)
    private String id;

    @Column(name = "state")
    private String state;
}
