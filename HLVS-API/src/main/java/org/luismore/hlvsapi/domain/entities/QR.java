package org.luismore.hlvsapi.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "qrs")
public class QR {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uniqueID;

    @Column(name = "token")
    private String token;

    @JsonIgnore
    private LocalDate expDate;

    @JsonIgnore
    private LocalTime expTime;

    @JsonIgnore
    private Boolean used;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_request")
    @JsonIgnore
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_qr_limit")
    @JsonIgnore
    private QRLimit qrLimit;
}
