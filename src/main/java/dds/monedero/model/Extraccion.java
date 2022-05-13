package dds.monedero.model;

import java.time.LocalDate;

public class Extraccion extends Movimiento {
  public Extraccion(LocalDate fecha, Double monto) {
    super(fecha, monto);
  }

  public Double calcularValor(Double saldo) {
    return saldo - getMonto();
  }
}
