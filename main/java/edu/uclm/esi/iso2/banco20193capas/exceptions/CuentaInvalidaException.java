/**Versión de mantenimiento 2.0.3**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class CuentaInvalidaException extends Exception {

    public CuentaInvalidaException(Long numero) {
        super("La cuenta " + numero + " no existe");
    }

}
