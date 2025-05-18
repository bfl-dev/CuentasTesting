package cl.isoftcuentas.CuentasTesting.modelos;

import cl.isoftcuentas.CuentasTesting.modelos.enums.Rol;
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
public class Personal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private boolean activado;
    private String nombre;
    private String email;
    private String contrasenia;
    private String rut;
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;
}
