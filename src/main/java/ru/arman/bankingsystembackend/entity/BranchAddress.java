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
public class BranchAddress {

    @Id
    @Column(name = "branch_id")
    private Long id;

    private String city;
    private String street;
    private String house;

    @OneToOne
    @MapsId
    @JoinColumn(name = "branch_id")
    @JsonIgnore
    private Branch branch;
}
