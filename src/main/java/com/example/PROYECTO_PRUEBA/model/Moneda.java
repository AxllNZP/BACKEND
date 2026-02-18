package com.example.PROYECTO_PRUEBA.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "MONEDA")
public class  Moneda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_moneda")
    private Long idMoneda;

    @Column(name = "nombre",nullable = false, length = 50)
    private String nombre;

    @Column(name = "simbolo",nullable = false, length = 10)
    private String simbolo;

    @Column(name = "codigo",nullable = false, length = 3, unique = true)
    private String codigo;

    @OneToMany(mappedBy = "moneda")
    @JsonIgnore
    private List<Pago> pagos;


}
