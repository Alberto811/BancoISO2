/**Versión de mantenimiento 2.0.3**/
package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class LoginException extends Exception {
    public LoginException() {
        super("Credenciales inválidas");
    }
}
