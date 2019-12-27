/**Versión 2.0.3 de mantenimiento**/
package edu.uclm.esi.iso2.banco20193capas.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.uclm.esi.iso2.banco20193capas.model.MovimientoCuenta;

public interface MovimientoCuentaDAO extends
CrudRepository<MovimientoCuenta, Long> {
    List<MovimientoCuenta> findByCuentaId(Long id);
}
