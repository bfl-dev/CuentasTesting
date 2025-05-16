package cl.isoftcuentas.CuentasTesting.modelos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String email;
    private String contraseña;
    private String rut;
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;
    @Column(name = "es_socio")
    private boolean esSocio;

}
