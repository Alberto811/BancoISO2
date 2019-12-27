/**Versión de mantenimiento 2.0.3**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class CuentaYaCreadaException extends Exception {
    public CuentaYaCreadaException() {
        super("La cuenta está creada y no admite añadir titulares");
    }
}
