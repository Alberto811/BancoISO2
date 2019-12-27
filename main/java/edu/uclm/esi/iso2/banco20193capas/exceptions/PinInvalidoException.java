/**Versión de mantenimiento 2.0.3**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class PinInvalidoException extends Exception {
    public PinInvalidoException() {
        super("PIN inválido");
    }
}
