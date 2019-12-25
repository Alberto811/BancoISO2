package edu.uclm.esi.iso2.banco20193capas;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaInvalidaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ImporteInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.PinInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.SaldoInsuficienteException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TarjetaBloqueadaException;
import edu.uclm.esi.iso2.banco20193capas.model.Cliente;
import edu.uclm.esi.iso2.banco20193capas.model.Cuenta;
import edu.uclm.esi.iso2.banco20193capas.model.Manager;
import edu.uclm.esi.iso2.banco20193capas.model.TarjetaCredito;
import edu.uclm.esi.iso2.banco20193capas.model.TarjetaDebito;
import junit.framework.TestCase;


	public class TestCuentaConFixturesDavidIllescas extends TestCase {
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
		public void testTCreditoComprarInternetImporteInvalido() {
			try {
				this.tcAna.comprar(1234, -3);
			} catch (ImporteInvalidoException e) {

			} catch (Exception e) {
				fail ("Esperaba ImporteInvalidoException pero he recibido :"+e);
			}
			assertTrue(this.tcAna.getCreditoDisponible() == 10000);
		}

		@Test
		public void testTCreditoComprarExcederSaldo() {

			  try {
				this.tcAna.comprar(1234, 10010);
			  } catch (TarjetaBloqueadaException | SaldoInsuficienteException e) {

			  }catch (Exception e) {
				fail ("Esperaba TarjetaBloqueadaException :"+e);
			}

			assertTrue(this.tcAna.getCreditoDisponible() == 10000);


		}


		@Test
		public void testTCPonerMismoPinNuevo() {
			try {
				this.tcPepe.cambiarPin(1234, 1234);
			}catch(PinInvalidoException e) {

			}catch(Exception e) {
				fail("Esperaba PinInvalidoException pero he recibido"+ e);
			}


		}


		@Test
		public void testTCCambiarPinOK () {
			try {
				this.tcPepe.cambiarPin(1234, 1344);
			}catch (Exception e) {
				fail ("No esperaba ninguna excepción");
			}
		}

		@Test
		public void testTCCreditoRestanteMal() {
			try {
				this.tcPepe.sacarDinero(1234, 1000);
				this.tcAna.sacarDinero(1234, 9000);
			} catch(Exception e) {
				fail("Esperaba Exception:"+ e);
			}
			assertFalse(this.tcPepe.getCreditoDisponible() == 500);
			assertFalse(this.tcAna.getCreditoDisponible() == 3000);



		}

		@Test
		public void testTCSacarDinero() {
			try {
				this.tcPepe.sacarDinero(1234, 1000);
				this.tcAna.sacarDinero(1234, 9000);
				assertTrue(this.tcPepe.getCreditoDisponible() == 997);
				assertTrue(this.tcAna.getCreditoDisponible() == 997);

			} catch (Exception e) {
				fail("No esperaba ninguna excepción pero he recibido:"+e);
			}

		}



		@Test
		public void testTCComprarPorInternetMal() {
			try {

					this.cuentaPepe.retirar(200);
					assertTrue(this.cuentaPepe.getSaldo()==800);

					int token = this.tcPepe.comprarPorInternet(tcPepe.getPin(), 300);
					assertTrue(this.tcPepe.getCreditoDisponible()==2000);
					this.tcPepe.confirmarCompraPorInternet(token);
					assertFalse(this.tcPepe.getCreditoDisponible()==1600);
					this.tcPepe.liquidar();
					assertTrue(this.tcPepe.getCreditoDisponible()==2000);
					assertTrue(cuentaPepe.getSaldo()==500);

				} catch (Exception e) {
					fail("Excepción inesperada debido a un error aritmético: " + e.getMessage());
				}

		}


		@Test
		public void testTCliquidar() {
			try {
				this.tcAna.liquidar();
			} catch (Exception e) {
				fail ("No se ha podido liquidar y se ha lanzado una excepción inesperada: "+ e);
			}


		}


		@After
		public void tearDown() {
			try {
				Manager.getMovimientoDAO().deleteAll();
				Manager.getMovimientoTarjetaCreditoDAO().deleteAll();
				Manager.getTarjetaCreditoDAO().deleteAll();
				Manager.getTarjetaDebitoDAO().deleteAll();
				Manager.getCuentaDAO().deleteAll();
				Manager.getClienteDAO().deleteAll();
			} catch (Exception e) {
				fail ("Excepción al liberar recursos con tearDown(): "+ e);
			}
		}

	}
