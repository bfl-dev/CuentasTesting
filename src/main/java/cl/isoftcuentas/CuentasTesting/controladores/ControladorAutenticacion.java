package cl.isoftcuentas.CuentasTesting.controladores;

import cl.isoftcuentas.CuentasTesting.dtos.RespuestaGenericaDTO;
import cl.isoftcuentas.CuentasTesting.dtos.SolicitudAutenticacionDTO;
import cl.isoftcuentas.CuentasTesting.seguridad.DetallesUsuario;
import cl.isoftcuentas.CuentasTesting.seguridad.ServicioAutenticacionUsuario;
import cl.isoftcuentas.CuentasTesting.utils.MaestroGalleta;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/cuentas")
public class ControladorAutenticacion {

    private final ServicioAutenticacionUsuario servicioAutenticacionUsuario;

    // Aquí puedes implementar los métodos para manejar la autenticación
    // Por ejemplo, un método para iniciar sesión, cerrar sesión, etc.
    // Puedes usar @PostMapping, @GetMapping, etc. según sea necesario

    @PostMapping
    @RequestMapping("/iniciar-sesion")
    public ResponseEntity<RespuestaGenericaDTO<DetallesUsuario>> inicioSesion(@RequestBody SolicitudAutenticacionDTO solicitudAutenticacion, HttpServletResponse response) {

        Optional<String> token = servicioAutenticacionUsuario.iniciarSesionUsuario(solicitudAutenticacion);

        if (token.isPresent()) {
            MaestroGalleta.crearCookie(response, "AuthToken", token.get(), true, 3600, "localhost");
            return ResponseEntity.ok(
                    new RespuestaGenericaDTO<>(true, servicioAutenticacionUsuario.conseguirDetallesUsuario(solicitudAutenticacion.rut()))
            );
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/cerrar-sesion")
    public ResponseEntity<String> cerrarSesion(HttpServletResponse response) {
        MaestroGalleta.limpiarCookie(response, "AuthToken");
        return ResponseEntity.ok("Sesión cerrada");
    }

    @PostMapping("/registrar")
    public String registrar() {
        return "Register successful";
    }

    @PostMapping("/pruebas")
    public ResponseEntity<String> pruebas(@RequestBody SolicitudAutenticacionDTO solicitudAutenticacion) {
        return ResponseEntity.ok("Pruebas exitosas");
    }

}
