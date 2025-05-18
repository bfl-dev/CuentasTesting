package cl.isoftcuentas.CuentasTesting.seguridad;

import cl.isoftcuentas.CuentasTesting.dtos.SolicitudAutenticacionDTO;
import cl.isoftcuentas.CuentasTesting.modelos.Cliente;
import cl.isoftcuentas.CuentasTesting.modelos.Personal;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioCliente;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioPersonal;
import cl.isoftcuentas.CuentasTesting.utils.JwtUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Transactional
@AllArgsConstructor
@Service
public class ServicioAutenticacionUsuario implements UserDetailsService { //Integracion chupame el pico

    private final RepositorioCliente repositorioCliente;
    private final RepositorioPersonal repositorioPersonal;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    public Optional<String> iniciarSesionUsuario(SolicitudAutenticacionDTO solicitudAutenticacion) {

        String rut = solicitudAutenticacion.rut();
        String contrasenia = solicitudAutenticacion.contrasenia();

        Authentication autenticacion = autenticar(rut, contrasenia);

        if (autenticacion.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(autenticacion);
            return Optional.of(jwtUtils.createToken(autenticacion));
        } else {
            return Optional.empty();
        }
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



    private Authentication autenticar(String rut, String contrasenia) {

        Optional<UserDetails> userDetails = Optional.ofNullable(loadUserByUsername(rut));

        if ( userDetails.isEmpty() || !passwordEncoder.matches(contrasenia, userDetails.get().getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        return new UsernamePasswordAuthenticationToken(userDetails.get().getUsername(), null, userDetails.get().getAuthorities());
    }


    public DetallesUsuario conseguirDetallesUsuario(String rut) {

        if (rut == null || rut.isEmpty()) {
            throw new BadCredentialsException("Rut no puede ser nulo o vacío");
        }

        if (repositorioPersonal.existsByRut(rut)) {
            Optional<Personal> personal = repositorioPersonal.findByRut(rut);
            if (personal.isPresent()) {
                return new DetallesUsuario(personal.get().getId()
                        , personal.get().getNombre()
                        , personal.get().getRol().toString()
                        , personal.get().getSucursal().getId());
            }
        }

        if (repositorioCliente.existsByRut(rut)) {
            Optional<Cliente> cliente = repositorioCliente.findByRut(rut);
            if (cliente.isPresent()) {
                return new DetallesUsuario(cliente.get().getId()
                        , cliente.get().getNombre()
                        , "CLIENTE"
                        , 0);
            }
        }

        throw new BadCredentialsException("Usuario no encontrado");
    }
}
