package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void Poner() {
    cuenta.poner(1500.00);
    assertEquals( 1500.00 , cuenta.getSaldo());
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500.00));
  }

  @Test
  void TresDepositos() {
    cuenta.poner(1500.00);
    cuenta.poner(456.00);
    cuenta.poner(1900.00);

    assertEquals( 3 , cuenta.getDepositos().size());
    assertEquals( 3856.00 , cuenta.getSaldo());
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500.00);
          cuenta.poner(456.00);
          cuenta.poner(1900.00);
          cuenta.poner(245.00);
    });
  }

  @Test
  void Sacar() {
    cuenta.setSaldo(1000.00);
    cuenta.sacar(400.00);
    assertEquals( 600.00 , cuenta.getSaldo());
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90.00);
          cuenta.sacar(1001.00);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000.00);
      cuenta.sacar(1001.00);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500.00));
  }

  @Test
  public void GetMontoExtraidoEnUnDia() {
    LocalDate fechaAfiltrar = LocalDate.of(2020, 1, 8);
    List<Extraccion> extracciones = new ArrayList<>();
    extracciones.add(new Extraccion(LocalDate.now(), 1000.00));
    extracciones.add(new Extraccion(fechaAfiltrar, 2000.00));
    extracciones.add(new Extraccion(fechaAfiltrar, 500.00));
    cuenta.setExtracciones(extracciones);

    assertEquals(2500.00, cuenta.getMontoExtraidoA(fechaAfiltrar));

  }

}