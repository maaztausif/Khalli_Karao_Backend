package com.maaztausif.khallikarao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MessageCategories")
@Getter
@Setter
public class MessageCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false,unique = true,length = 100)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

}
