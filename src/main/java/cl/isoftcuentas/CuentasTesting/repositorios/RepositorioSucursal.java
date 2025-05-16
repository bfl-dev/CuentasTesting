package cl.isoftcuentas.CuentasTesting.repositorios;

import cl.isoftcuentas.CuentasTesting.modelos.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioSucursal extends JpaRepository<Sucursal, Integer> {
}
