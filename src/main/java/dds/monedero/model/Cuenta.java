package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cuenta {

  private Double saldo = 0.0;
  private List<Extraccion> extracciones = new ArrayList<>();
  private List<Deposito> depositos = new ArrayList<>();

  public Cuenta() {
    saldo = 0.0;
  }

  public Cuenta(Double montoInicial) {
    saldo = montoInicial;
  }

  public void setExtracciones(List<Extraccion> extracciones) {
    this.extracciones = extracciones;
  }

  public void setDepositos(List<Deposito> depositos) {
    this.depositos = depositos;
  }

  public void poner(Double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (depositos.size() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    agregarDeposito(new Deposito(LocalDate.now(), cuanto));
  }

  public void sacar(Double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    if (cuanto > calcularLimite()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + calcularLimite());
    }

    agregarExtraccion(new Extraccion(LocalDate.now(), cuanto));
  }

  public Double getMontoExtraidoA(LocalDate fecha) {
    return this.extracciones.stream()
        .filter(extraccion -> extraccion.esDeLaFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return Stream.concat(extracciones.stream(), depositos.stream())
        .collect(Collectors.toList());
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  private Double calcularLimite() {
    Double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    return 1000 - montoExtraidoHoy;
  }

  private void actualizarSaldo(Movimiento movimiento) {
    setSaldo(movimiento.calcularValor(getSaldo()));
  }

  private void agregarDeposito(Deposito deposito) {
    depositos.add(deposito);
    actualizarSaldo(deposito);
  }

  private void agregarExtraccion(Extraccion extraccion) {
    extracciones.add(extraccion);
    actualizarSaldo(extraccion);
  }
}
