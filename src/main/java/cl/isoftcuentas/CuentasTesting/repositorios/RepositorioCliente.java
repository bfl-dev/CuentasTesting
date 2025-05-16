package cl.isoftcuentas.CuentasTesting.repositorios;

import cl.isoftcuentas.CuentasTesting.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCliente extends JpaRepository<Cliente, Integer> {
}
