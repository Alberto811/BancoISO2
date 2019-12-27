/**Versión 2.0.3 de mantenimiento**/
package edu.uclm.esi.iso2.banco20193capas.dao;

import org.springframework.data.repository.CrudRepository;

import edu.uclm.esi.iso2.banco20193capas.model.Cuenta;

public interface CuentaDAO extends CrudRepository<Cuenta, Long> {

}
