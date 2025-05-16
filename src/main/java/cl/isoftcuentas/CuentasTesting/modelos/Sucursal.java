package cl.isoftcuentas.CuentasTesting.modelos;

import cl.isoftcuentas.CuentasTesting.modelos.enums.TipoSucursal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String direccion;
    @Column(name = "correo_contacto")
    private String correoContacto;
    @Column(name = "tipo_sucursal")
    @Enumerated(EnumType.STRING)
    private TipoSucursal tipoSucursal;
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Personal> personal;
}
