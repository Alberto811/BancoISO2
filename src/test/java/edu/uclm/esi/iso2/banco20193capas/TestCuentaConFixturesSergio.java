package edu.uclm.esi.iso2.banco20193capas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.uclm.esi.iso2.banco20193capas.exceptions.ImporteInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.PinInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TarjetaBloqueadaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TokenInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.model.Cliente;
import edu.uclm.esi.iso2.banco20193capas.model.Cuenta;
import edu.uclm.esi.iso2.banco20193capas.model.Manager;
import edu.uclm.esi.iso2.banco20193capas.model.TarjetaDebito;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCuentaConFixturesSergio extends TestCase {
	private Cuenta cuentaPepe, cuentaAna;
	private Cliente pepe, ana;
	private TarjetaDebito tdPepe, tdAna;

	@Before
	public void setUp() {
		Manager.getMovimientoDAO().deleteAll();
		Manager.getMovimientoTarjetaCreditoDAO().deleteAll();
		Manager.getTarjetaCreditoDAO().deleteAll();
		Manager.getTarjetaDebitoDAO().deleteAll();
		Manager.getCuentaDAO().deleteAll();
		Manager.getClienteDAO().deleteAll();

		this.pepe = new Cliente("12345X", "Pepe", "Pérez");
		this.pepe.insert();
		this.ana = new Cliente("98765F", "Ana", "López");
		this.ana.insert();
		this.cuentaPepe = new Cuenta(1);
		this.cuentaAna = new Cuenta(2);
		try {
			this.cuentaPepe.addTitular(pepe);
			this.cuentaPepe.insert();
			this.cuentaPepe.ingresar(1000);
			this.cuentaAna.addTitular(ana);
			this.cuentaAna.insert();
			this.cuentaAna.ingresar(5000);
			this.tdPepe = this.cuentaPepe.emitirTarjetaDebito(pepe.getNif());
			this.tdPepe.cambiarPin(this.tdPepe.getPin(), 1234);
			this.tdAna = this.cuentaAna.emitirTarjetaDebito(ana.getNif());
			this.tdAna.cambiarPin(this.tdAna.getPin(), 1234);
		} catch (Exception e) {
			fail("Excepción inesperada en setUp(): " + e);
		}

	}

	@Test
	public void testTDSacarDineroOK() {
		try {
			this.tdPepe.sacarDinero(1234, 1000);
			this.tdAna.sacarDinero(1234, 4000);
			assertTrue(this.tdPepe.getCuenta().getSaldo() == 0);
			assertTrue(this.tdAna.getCuenta().getSaldo() == 1000);

		} catch (Exception e) {
			fail("No esperaba ninguna excepción");
		}

	}

	@Test
	public void testTDComprarImporteInvalido() {
		try {
			this.tdPepe.comprar(1234, -1);
		} catch (ImporteInvalidoException e) {

		} catch (Exception e) {
			fail("Esperaba ImporteInvalidoException pero he recibido" + e);
		}

		assertTrue(this.cuentaPepe.getSaldo() == 1000);

	}

	@Test
	public void testCompraPorInternetConTDOK() {

		try {

			int token = this.tdPepe.comprarPorInternet(tdPepe.getPin(), 300);
			assertTrue(this.cuentaPepe.getSaldo() == 1000);
			tdPepe.confirmarCompraPorInternet(token);
			assertTrue(this.cuentaPepe.getSaldo() == 700);
			assertTrue(this.tdPepe.getCuenta().getSaldo() == 700);
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
	}

	@Test
	public void testCompraPorInternetConTDTokenInvalido() {

		try {

			this.tdPepe.comprarPorInternet(this.tdPepe.getPin(), 300);
			this.tdPepe.confirmarCompraPorInternet(-1);

		} catch (TokenInvalidoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
		assertTrue(this.tdPepe.getCuenta().getSaldo() == 1000);
	}

	@Test
	public void testCambiarPinPinInvalido() {

		try {

			this.tdPepe.cambiarPin(1, 1);

		} catch (PinInvalidoException e) {
		} catch (Exception e) {
			fail("Excepción inesperada: " + e.getMessage());
		}
		assertTrue(this.tdPepe.getPin() == 1234);
	}

	@Test
	public void testTarjetaBloqueada() {

		try {
			this.tdPepe.comprar(1, 100);

		} catch (PinInvalidoException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido" + e);
		}
		try {
			this.tdPepe.comprar(1, 100);

		} catch (PinInvalidoException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido" + e);
		}

		try {
			this.tdPepe.comprar(1, 100);

		} catch (PinInvalidoException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido" + e);
		}

		try {
			this.tdPepe.comprar(1, 100);

		} catch (TarjetaBloqueadaException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			fail("Esperaba TarjetaBloqueadaException pero he recibido" + e);
		}

		assertTrue(tdPepe.getCuenta().getSaldo() == 1000);
	}

}
