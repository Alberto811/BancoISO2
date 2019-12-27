/**Versión 2.0.3 de mantenimiento**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class PinInvalidoException extends Exception {
    public PinInvalidoException() {
        super("PIN inválido");
    }
}
