/**Versión 2.0.3 de mantenimiento**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class CuentaInvalidaException extends Exception {

    public CuentaInvalidaException(Long numero) {
        super("La cuenta " + numero + " no existe");
    }

}
