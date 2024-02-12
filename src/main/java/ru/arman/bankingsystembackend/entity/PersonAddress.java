package ru.arman.bankingsystembackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PersonAddress {

    @Id
    @Column(name = "person_id")
    private Long id;

    private String city;
    private String street;
    private String house;
    private String apt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "person_id")
    @JsonIgnore
    private Person person;
}
