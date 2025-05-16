package cl.isoftcuentas.CuentasTesting.repositorios;

import cl.isoftcuentas.CuentasTesting.modelos.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioPersonal extends JpaRepository<Personal, Integer> {
}
