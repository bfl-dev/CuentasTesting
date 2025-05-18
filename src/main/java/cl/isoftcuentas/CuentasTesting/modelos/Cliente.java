package cl.isoftcuentas.CuentasTesting.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String email;
    private String contrasenia;
    private String rut;
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;
    @Column(name = "es_socio")
    private boolean esSocio;

}
