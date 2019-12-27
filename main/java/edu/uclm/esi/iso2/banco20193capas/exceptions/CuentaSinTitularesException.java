/**Versión de mantenimiento 2.0.3**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class CuentaSinTitularesException extends Exception {
    public CuentaSinTitularesException() {
        super("Falta indicar el titular o titulares");
    }
}
