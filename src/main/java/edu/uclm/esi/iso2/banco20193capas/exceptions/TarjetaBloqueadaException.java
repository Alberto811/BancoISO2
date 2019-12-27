/**Versión 2.0.3 de mantenimiento**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class TarjetaBloqueadaException extends Exception {
    public TarjetaBloqueadaException() {
        super("La tarjeta está bloqueada");
    }
}
