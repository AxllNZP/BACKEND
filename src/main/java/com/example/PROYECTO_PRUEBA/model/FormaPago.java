package com.example.PROYECTO_PRUEBA.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "forma_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class FormaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_forma_pago")
    @ToString.Include
    private Long idFormaPago;

    @Column(nullable = false, length = 50)
    private FormasPagos nombre;

    @Column(length = 100)
    private String descripcion;

    @OneToMany(mappedBy = "formaPago",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos;

    @PrePersist
    public void pre(){
        if(descripcion == null){
            descripcion = "SIN DESCRIPCION";
        }
        if(nombre == null){
            nombre = FormasPagos.efectivo;
        }
    }
}
