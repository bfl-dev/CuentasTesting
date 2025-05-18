package cl.isoftcuentas.CuentasTesting.dtos;

public record RespuestaGenericaDTO<T>(boolean exito, T respuesta ) { }
