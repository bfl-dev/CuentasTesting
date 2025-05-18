package cl.isoftcuentas.CuentasTesting.repositorios;

import cl.isoftcuentas.CuentasTesting.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioCliente extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByRut(String rut);

    boolean existsByRut(String rut);
}
