package cl.isoftcuentas.CuentasTesting.seguridad;

import cl.isoftcuentas.CuentasTesting.dtos.SolicitudAutenticacionDTO;
import cl.isoftcuentas.CuentasTesting.dtos.SolicitudRegistroDTO;
import cl.isoftcuentas.CuentasTesting.modelos.Cliente;
import cl.isoftcuentas.CuentasTesting.modelos.Personal;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioCliente;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioPersonal;
import cl.isoftcuentas.CuentasTesting.utils.JwtUtilidad;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;


@Transactional
@AllArgsConstructor
@Service
public class ServicioAutenticacionUsuario implements UserDetailsService { //Integracion chupadme el pico

    private final RepositorioCliente repositorioCliente;
    private final RepositorioPersonal repositorioPersonal;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilidad jwtUtilidad;


    public String iniciarSesionUsuario(SolicitudAutenticacionDTO solicitudAutenticacion) {
        String rut = solicitudAutenticacion.rut();
        String contrasenia = solicitudAutenticacion.contrasenia();

        autenticar(rut, contrasenia);
        UserDetails userDetails = loadUserByUsername(rut);
        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("CLIENTE");

        return jwtUtilidad.createToken(userDetails.getUsername(), rol);
    }

    public boolean registrar(SolicitudRegistroDTO solicitudRegistro) {

        String rut = solicitudRegistro.rut();

        if (repositorioCliente.existsByRut(rut)) return false;

        Cliente nuevoCliente = Cliente.builder()
                .nombre(solicitudRegistro.nombre())
                .email(solicitudRegistro.email())
                .esSocio(false)
                .fechaCreacion(Date.valueOf(LocalDate.now()))
                .contrasenia(passwordEncoder.encode(solicitudRegistro.contrasenia()))
                .rut(solicitudRegistro.rut())
                .nombre(solicitudRegistro.nombre()).build();


        repositorioCliente.save(nuevoCliente);

        return true;
    }



    @Override
    public UserDetails loadUserByUsername(String rut) throws UsernameNotFoundException {
        if (repositorioPersonal.existsByRut(rut)){
            Optional<Personal> personal = repositorioPersonal.findByRut(rut);
            if (personal.isPresent()) {
                return rellenarPorPersonal(personal.get());
            }
        } else if (repositorioCliente.existsByRut(rut)) {
            Optional<Cliente> cliente = repositorioCliente.findByRut(rut);
            if (cliente.isPresent()) {
                return rellenarPorCliente(cliente.get());
            }
        }
        throw new UsernameNotFoundException("Usuario no encontrado");
    }


    private UserDetails rellenarPorPersonal(Personal personal) {
        entityManager.refresh(personal);
        return User.builder()
                .username(personal.getRut())
                .password(personal.getContrasenia())
                .roles(personal.getRol().toString())
                .disabled(!personal.isActivado())
                .build();
    }

    private UserDetails rellenarPorCliente(Cliente cliente) {
        entityManager.refresh(cliente);
        return User.builder()
                .username(cliente.getRut())
                .password(cliente.getContrasenia())
                .roles("CLIENTE")
                .build();
    }



    private void autenticar(String rut, String contrasenia) {
        UserDetails userDetails = loadUserByUsername(rut);
        if (!passwordEncoder.matches(contrasenia, userDetails.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }


    public DetallesUsuario conseguirDetallesUsuario(String rut) {

        if (rut == null || rut.isEmpty()) {
            throw new BadCredentialsException("Rut no puede ser nulo o vacío");
        }

        Optional<DetallesUsuario> detallesUsuario = Optional.empty();

        if (repositorioCliente.existsByRut(rut)) {
            detallesUsuario = Optional.of(conseguirDetallesCliente(rut));
        }

        if (repositorioPersonal.existsByRut(rut)) {
            detallesUsuario = Optional.of(conseguirDetallesPersonal(rut));
        }


        if (detallesUsuario.isPresent()) return detallesUsuario.get();

        throw new BadCredentialsException("Usuario no encontrado");
    }

    private DetallesUsuario conseguirDetallesPersonal(String rut) {
        Optional<Personal> personal = repositorioPersonal.findByRut(rut);
        return personal.map(value -> new DetallesUsuario(value.getId()
                , value.getNombre()
                , value.getRol().toString()
                , value.getSucursal().getId())
        ).orElse(null);
    }

    private DetallesUsuario conseguirDetallesCliente(String rut) {
        Optional<Cliente> cliente = repositorioCliente.findByRut(rut);
        return cliente.map(value -> new DetallesUsuario(value.getId()
                , value.getNombre()
                , "CLIENTE"
                , 0)
        ).orElse(null);
    }
}
