package cl.isoftcuentas.CuentasTesting.repositorios;

import cl.isoftcuentas.CuentasTesting.modelos.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioPersonal extends JpaRepository<Personal, Integer> {
    boolean existsByRut(String rut);
    boolean existsByEmail(String email);

    Optional<Personal> findByRut(String rut);
    Optional<Personal> findByEmail(String email);
}
