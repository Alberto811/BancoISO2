/**Versión de mantenimiento 2.0.3**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException() {
        super("Saldo insuficiente para el importe solicitado");
    }
}
