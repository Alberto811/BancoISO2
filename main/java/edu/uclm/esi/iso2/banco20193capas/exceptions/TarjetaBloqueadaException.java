/**Versión de mantenimiento 2.0.3**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class TarjetaBloqueadaException extends Exception {
    public TarjetaBloqueadaException() {
        super("La tarjeta está bloqueada");
    }
}
