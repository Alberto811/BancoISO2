package edu.uclm.esi.iso2.banco20193capas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.uclm.esi.iso2.banco20193capas.exceptions.ClienteNoAutorizadoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ClienteNoEncontradoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaInvalidaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaYaCreadaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ImporteInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.PinInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.SaldoInsuficienteException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TarjetaBloqueadaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TokenInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.model.Cliente;
import edu.uclm.esi.iso2.banco20193capas.model.Cuenta;
import edu.uclm.esi.iso2.banco20193capas.model.Manager;
import edu.uclm.esi.iso2.banco20193capas.model.TarjetaCredito;
import edu.uclm.esi.iso2.banco20193capas.model.TarjetaDebito;
import junit.framework.TestCase;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestCuentaConFixturesAlberto extends TestCase {
	private Cuenta cuentaPepe, cuentaAna;
	private Cliente pepe, ana;
	private TarjetaDebito tdPepe, tdAna;
	private TarjetaCredito tcPepe, tcAna;

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
			this.tcPepe = this.cuentaPepe.emitirTarjetaCredito(pepe.getNif(), 2000);
			this.tcPepe.cambiarPin(this.tcPepe.getPin(), 1234);
			this.tcAna = this.cuentaAna.emitirTarjetaCredito(ana.getNif(), 10000);
			this.tcAna.cambiarPin(this.tcAna.getPin(), 1234);
			this.tdPepe = this.cuentaPepe.emitirTarjetaDebito(pepe.getNif());
			this.tdPepe.cambiarPin(this.tdPepe.getPin(), 1234);
			this.tdAna = this.cuentaAna.emitirTarjetaDebito(ana.getNif());
			this.tdAna.cambiarPin(this.tdAna.getPin(), 1234);
		} catch (Exception e) {
			fail("Excepción inesperada en setUp(): " + e);
		}

	}

	@Test
	public void testTCSacarDinero() {
		try {
			this.tcPepe.sacarDinero(1234, 1000);
			this.tcAna.sacarDinero(1234, 9000);
			assertTrue(this.tcPepe.getCreditoDisponible() == 997);
			assertTrue(this.tcAna.getCreditoDisponible() == 997);

		} catch (Exception e) {
			fail("No esperaba ninguna excepción");
		}

	}

	@Test
	public void testTCComprarPorInternet() {
		try {
			this.tcPepe.setActiva(false);
			this.tcPepe.comprarPorInternet(1234, 1999);
		} catch(TarjetaBloqueadaException e) {
			
		}catch(Exception e) {
			fail("Esperaba TarjetaBloqueadaException pero he recibido"+e);
		}
		
		assertTrue(this.tcPepe.getCreditoDisponible() == 2000);

	}
	
	@Test
	public void testTCComprarPorInternetTokenException() {
		try {
			int token = this.tcPepe.comprarPorInternet(1234, 1999);
			this.tcPepe.confirmarCompraPorInternet(88);
			
			
		} catch(TokenInvalidoException e) {
			
		}catch(Exception e) {
			fail("Esperaba TokenInvalidoException pero he recibido"+e);
		}
		
		assertFalse(this.tcPepe.getCreditoDisponible() == 1);

	}
	
	@Test
	public void testTCComprar() {
		try {
			this.tcPepe.comprar(1234, -1);
		} catch( ImporteInvalidoException e) {
			
		}catch(Exception e) {
			fail("Esperaba ImporteInvalidoException pero he recibido"+e);
		}
		
		assertTrue(this.tcPepe.getCreditoDisponible() == 2000);

	}
	
	
	@Test
	public void testTCLiquidar() {
		try {
			this.tcPepe.liquidar();
		}catch(Exception e) {
			fail("No se esperaba ninguna excepción pero he recibido"+e);
		}
		
		assertTrue(this.cuentaPepe.getSaldo() == 1000);
	}
	
	@Test
	public void testTCCreditoRestante() {
		try {
			this.tcPepe.sacarDinero(1234, 1000);
		}catch(Exception e) {
			fail("No se esperaba ninguna excepción pero he recibido"+e);
		}
		
		assertTrue(this.tcPepe.getCreditoDisponible() == 997);
	}
	
	
	@Test
	public void testTCCambiarPin() {
		try {
			this.tcPepe.cambiarPin(1245, 4567);
		}catch(PinInvalidoException e) {
			
		}catch(Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido"+e);
		}
		
		assertFalse(this.tcPepe.getPin() == 4567);
	}
	
	
	@Test
	public void testTDSacarDinero() {
		try {
			this.tdPepe.sacarDinero(1234, 400);;
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		
		}
		
	}
	
	@Test
	public void testTDComprarPorInternet() {
		try {
			int token = this.tdAna.comprarPorInternet(1234, 200);
			this.tdAna.confirmarCompraPorInternet(token);
			
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		assertTrue(this.tdAna.getCuenta().getSaldo() == 4800);
		
	}
	
	@Test
	public void testTDBloquear() {
		try {
			this.tdPepe.sacarDinero(123, 200);
		}catch( PinInvalidoException e) {
			
		}catch(Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido "+e);
		}
		try {
			this.tdPepe.sacarDinero(123, 200);
		}catch( PinInvalidoException e) {
			
			
		}catch(Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido "+e);
		}
		try {
			this.tdPepe.sacarDinero(123, 200);
		}catch( PinInvalidoException e) {
			
			
		}catch(Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido "+e);
		}
		assertTrue(this.tdPepe.isActiva() == false);
		
	}
	
	
	@Test
	public void testCambiarPin() {
		try {
			this.tdPepe.cambiarPin(123, 123);
		}catch( PinInvalidoException e) {
			
		
		}catch(Exception e) {
			fail("Esperaba PinInvalidoException pero he recibido"+e);
		}
		
	}

	
	@Test
	public void testTCTitular() {
		try {
			assertTrue(this.tcPepe.getTitular().getNombre().equals("Pepe"));
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	@Test
	public void testTCSetPin() {
		try {
			this.tcAna.setPin(12);
			assertFalse(this.tcAna.getPin() == 1234);
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	@Test
	public void testTCSetID() {
		try {
			this.tcAna.setId(875L);
			assertTrue(this.tcAna.getId() == 875L);
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	
	@Test
	public void testClienteGetApellidos() {
		try {
			assertTrue(ana.getApellidos().equals("López"));
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	@Test
	public void testClienteSetApellidos() {
		try {
			ana.setApellidos("Castell");
			assertFalse(ana.getApellidos().equals("López"));
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	
	@Test
	public void testClienteSetNombre() {
		try {
			pepe.setNombre("Paco");
			assertTrue(pepe.getNombre().equals("Paco"));
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	
	@Test
	public void testClienteSetNIF() {
		try {
			ana.setNif("05989774A");
			assertFalse(ana.getNif().equals("98765F"));
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	@Test
	public void testClienteSetId() {
		try {
			ana.setId(875L);
			assertTrue(ana.getId() == 875L);
		}catch( Exception e) {
			fail("No esperaba ninguna excepción pero he recibido"+e);
		}
		
	}
	
	@Test
	public void testCuentaAddTitular() {
		try {
			Cuenta cuentaPrueba = new Cuenta(3);
			cuentaPrueba.addTitular(pepe);
			
		}catch( CuentaYaCreadaException e) {
			
		}catch(Exception e) {
			fail("Esperaba CuentaYaCreadaException pero he recibido"+e);
		}
		
	}
	
	
	@Test
	public void testCuentaIngresar() {
		try {
			this.cuentaAna.ingresar(-3);
			
		}catch( ImporteInvalidoException e) {
			
		}catch(Exception e) {
			fail("Esperaba ImporteInvalidoException pero he recibido"+e);
		}
		
	}
	
	@Test
	public void testCuentaRetirar() {
		try {
			this.cuentaPepe.retirar(0);
			
		}catch( ImporteInvalidoException e) {
			
		}catch(Exception e) {
			fail("Esperaba ImporteInvalidoException pero he recibido"+e);
		}
		
	}
	@Test
	public void testCuentaEmititTD() {
		try {
			Cuenta cuentaPrueba = new Cuenta(3);
			TarjetaDebito td = new TarjetaDebito();
			td = cuentaPrueba.emitirTarjetaDebito("05678345G");
			
		}catch( ClienteNoEncontradoException e) {
			
		}catch(Exception e) {
			fail("Esperaba ClienteNoEncontradoException pero he recibido"+e);
		}
		
	}
	
	@Test
	public void testCuentaClienteAutorizado() {
		try {
			Cuenta cuentaPrueba = new Cuenta(3);
			TarjetaDebito td = new TarjetaDebito();
			Cliente paco = new Cliente("05678345G", "Paco", "Castell");
			paco.insert();
			td = cuentaPrueba.emitirTarjetaDebito(paco.getNif());
			
		}catch( ClienteNoAutorizadoException e) {
			
		}catch(Exception e) {
			fail("Esperaba ClienteNoAutorizadoException pero he recibido"+e);
		}
		
	}
}
