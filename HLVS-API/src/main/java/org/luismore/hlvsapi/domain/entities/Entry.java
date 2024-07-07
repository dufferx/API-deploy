package org.luismore.hlvsapi.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "entries")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "entry_time")
    private LocalTime entryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entry_type")
    private EntryType entryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_house")
    private House house;

    @Column(name = "comment")
    private String comment;

    @Column(name = "DUI")
    private String dui;

    @Column(name = "headline")
    private String headline;
}
