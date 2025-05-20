package cl.isoftcuentas.CuentasTesting.servicios;

import cl.isoftcuentas.CuentasTesting.dtos.SolicitudCambioInformacionDTO;
import cl.isoftcuentas.CuentasTesting.modelos.InformacionUsuario;
import cl.isoftcuentas.CuentasTesting.modelos.Cliente;
import cl.isoftcuentas.CuentasTesting.modelos.Personal;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioCliente;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioPersonal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioInformacionCuenta {

    private final RepositorioPersonal repositorioPersonal;
    private final RepositorioCliente repositorioCliente;

    public Optional<InformacionUsuario> obtenerInformacionUsuario(String rut, String[] solicitudInformacion) {

        Optional<Personal> personal = repositorioPersonal.findByRut(rut);
        Optional<Cliente> cliente = repositorioCliente.findByRut(rut);

        if (personal.isPresent()){
            return Optional.of(mapearPersonal(personal.get(), List.of(solicitudInformacion)));
        } else if (cliente.isPresent()) {
            return Optional.of(mapearCliente(cliente.get(), List.of(solicitudInformacion)));
        }
        return Optional.empty();
    }

    public InformacionUsuario mapearPersonal(Personal personal, List<String> atributos) {
        return new InformacionUsuario(
                atributos.contains("nombre")?personal.getNombre(): null,
                atributos.contains("sucursal_id")?personal.getSucursal().getId().toString(): null,
                atributos.contains("rol")?personal.getRol().toString(): null,
                atributos.contains("activado")?personal.isActivado(): null,
                atributos.contains("nombre")?personal.getNombre(): null,
                atributos.contains("email")?personal.getEmail(): null,
                atributos.contains("rut")?personal.getRut(): null,
                atributos.contains("fecha_creacion")?personal.getFechaCreacion().toString(): null,
                null
        );
    }

    public InformacionUsuario mapearCliente(Cliente cliente, List<String> atributos) {
        return new InformacionUsuario(
                atributos.contains("id")?cliente.getId().toString(): null,
                null,
                null,
                null,
                atributos.contains("nombre")?cliente.getNombre(): null,
                atributos.contains("email")?cliente.getEmail(): null,
                atributos.contains("rut")?cliente.getRut(): null,
                atributos.contains("fecha_creacion")?cliente.getFechaCreacion().toString(): null,
                atributos.contains("es_socio")?cliente.isEsSocio(): null
        );
    }

    public boolean actualizarInformacionUsuario(String rut, SolicitudCambioInformacionDTO solicitudCambioInformacion) {
        Optional<Personal> personal = repositorioPersonal.findByRut(rut);
        Optional<Cliente> cliente = repositorioCliente.findByRut(rut);

        if (personal.isPresent()){
            Personal personalActualizado = personal.get();
            personalActualizado.setNombre(solicitudCambioInformacion.Nombre());
            personalActualizado.setEmail(solicitudCambioInformacion.Email());
            repositorioPersonal.save(personalActualizado);
            return true;
        } else if (cliente.isPresent()) {
            Cliente clienteActualizado = cliente.get();
            clienteActualizado.setNombre(solicitudCambioInformacion.Nombre());
            clienteActualizado.setEmail(solicitudCambioInformacion.Email());
            repositorioCliente.save(clienteActualizado);
            return true;
        }
        return false;
    }
}
