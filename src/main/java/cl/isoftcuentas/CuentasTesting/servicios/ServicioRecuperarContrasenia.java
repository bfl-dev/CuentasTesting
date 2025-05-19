package cl.isoftcuentas.CuentasTesting.servicios;

import cl.isoftcuentas.CuentasTesting.modelos.Cliente;
import cl.isoftcuentas.CuentasTesting.modelos.Personal;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioCliente;
import cl.isoftcuentas.CuentasTesting.repositorios.RepositorioPersonal;
import cl.isoftcuentas.CuentasTesting.utils.JwtUtilidad;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioRecuperarContrasenia {
    private final RepositorioPersonal repositorioPersonal;
    private final RepositorioCliente repositorioCliente;
    private final JavaMailSender enviadorEmail;
    private final PasswordEncoder codificadorContrasenia;
    private final JwtUtilidad jwtUtilidad;

    public void enviarEmailRecuperacionContrasenia(String email) {
       if (!repositorioCliente.existsByEmail(email) && !repositorioPersonal.existsByEmail(email)) {
           return;
       }

       String token = jwtUtilidad.generarTokenRecuperarContrasenia(email);
       String urlRecuperacion = "http://frontend.com/recuperar-contrasenia?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Recuperación de Contraseña");
        mensaje.setText("Para recuperar tu contraseña, haz clic en el siguiente enlace: " + urlRecuperacion);
        enviadorEmail.send(mensaje);
    }

    public void cambiarContrasenia(String token, String nuevaContrasenia) {
        String email = jwtUtilidad.validarTokenRecuperarContrasenia(token);
        if (email == null) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        Optional<Personal> personal = repositorioPersonal.findByEmail(email);
        Optional<Cliente> cliente = repositorioCliente.findByEmail(email);

        if (personal.isPresent()) {
            Personal usuario = personal.get();
            usuario.setContrasenia(codificadorContrasenia.encode(nuevaContrasenia));
            repositorioPersonal.save(usuario);
        } else if (cliente.isPresent()) {
            Cliente usuario = cliente.get();
            usuario.setContrasenia(codificadorContrasenia.encode(nuevaContrasenia));
            repositorioCliente.save(usuario);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
    }

}
