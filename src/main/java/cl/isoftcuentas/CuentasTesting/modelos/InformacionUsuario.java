package cl.isoftcuentas.CuentasTesting.modelos;


import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record InformacionUsuario(
        String id,
        String sucursal_id,
        String rol,
        Boolean activado,
        String nombre,
        String email,
        String rut,
        String fecha_creacion,
        Boolean es_socio
) {
}
