package cl.isoftcuentas.CuentasTesting.controladores;

import cl.isoftcuentas.CuentasTesting.modelos.InformacionUsuario;
import cl.isoftcuentas.CuentasTesting.servicios.ServicioInformacionCuenta;
import org.springframework.security.core.Authentication;
import cl.isoftcuentas.CuentasTesting.dtos.RespuestaGenericaDTO;
import cl.isoftcuentas.CuentasTesting.dtos.SolicitudCambioInformacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ControladorInformacion {

    private final ServicioInformacionCuenta servicioInformacionCuenta;

    @GetMapping("/cuenta/info")
    public ResponseEntity<RespuestaGenericaDTO<InformacionUsuario>> obtenerInformacion(
            @RequestParam String[] solicitudInformacion, Authentication authentication) // Inject Authentication
    {
        return servicioInformacionCuenta.obtenerInformacionUsuario(authentication.getName() ,solicitudInformacion)
                .map(informacionUsuario ->
                        ResponseEntity.ok(new RespuestaGenericaDTO<>(true, informacionUsuario)))
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }


    @PatchMapping("/cuenta/info")
    public ResponseEntity<RespuestaGenericaDTO<String>> actualizarCuenta(
            @RequestBody SolicitudCambioInformacionDTO solicitudCambioInformacion, Authentication authentication)
    {
        return servicioInformacionCuenta.actualizarInformacionUsuario(authentication.getName(), solicitudCambioInformacion)?
                ResponseEntity.ok(new RespuestaGenericaDTO<>(true, "Datos actualizados")):
                ResponseEntity.badRequest().body(new RespuestaGenericaDTO<>(false, "Error al actualizar datos"));
    }//the game
}
