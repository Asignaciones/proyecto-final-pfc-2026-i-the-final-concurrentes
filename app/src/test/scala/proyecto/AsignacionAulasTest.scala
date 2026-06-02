package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  // Ejemplo 1 del enunciado
  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  // solapan
  test("solapan: M01[4,8) y M02[6,10) se solapan") {
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan: M01[4,8) y M03[12,16) no se solapan") {
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos adyacentes [0,4) y [4,8) no se solapan") {
    assert(!solapan(("A", 0, 4, 10), ("B", 4, 8, 10)))
  }

  test("solapan: M01[4,8) y M03[12,16) no se solapan (disjuntos)") {
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos adyacentes [0,4) y [4,8) NO se solapan (frontera exacta)") {
    // fin del primero == ini del segundo → no hay intersección
    assert(!solapan(("A", 0, 4, 10), ("B", 4, 8, 10)))
  }

  test("solapan: un curso contenido dentro de otro se solapa") {
    // [2,8) contiene a [3,5) completamente
    assert(solapan(("X", 2, 8, 20), ("Y", 3, 5, 15)))
  }

  test("solapan: cursos idénticos en tiempo se solapan") {
    assert(solapan(("P", 6, 10, 30), ("Q", 6, 10, 30)))
  }

  test("solapan: simetría — solapan(c1,c2) == solapan(c2,c1)") {
    val cx = ("X", 4, 9, 20)
    val cy = ("Y", 7, 12, 20)
    assert(solapan(cx, cy) == solapan(cy, cx))
  }

  test("solapan: cursos separados sin contacto [0,2) y [5,8) no se solapan") {
    assert(!solapan(("A", 0, 2, 10), ("B", 5, 8, 10)))
  }

  // choques
  test("choques: asignacion [0,0,1] tiene 1 choque (M01 y M02 en E101)") {
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] no tiene choques") {
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  test("choques: asignacion [0,0,1] tiene 1 choque (M01 y M02 en E101)") {
    // M01 y M02 solapan y están en la misma aula 0 → 1 choque
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] no tiene choques") {
    // M01→E101, M02→E102 (no solapan entre sí con distinta aula),
    // M03→E101 pero no solapa con M01
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  test("choques: vector vacío de cursos da 0 choques") {
    assert(choques(Vector.empty, Vector.empty) == 0)
  }

  test("choques: aulas diferentes para cursos que solapan → 0 choques") {
    // M01 y M02 solapan pero están en aulas distintas
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  test("choques: un solo curso → 0 choques (no hay pares)") {
    assert(choques(Vector(("C0", 0, 4, 20)), Vector(0)) == 0)
  }

  // capacidadFallida
  test("capacidadFallida: asignacion [0,0,1] no falla capacidad") {
    assert(capacidadFallida(c1, a1, Vector(0, 0, 1)) == 0)
  }

  test("capacidadFallida: asignacion [0,0,1] del ejemplo 1 → 0 fallos") {
    // M01(25)→E101(30) ok, M02(30)→E101(30) ok, M03(20)→E102(40) ok
    assert(capacidadFallida(c1, a1, Vector(0, 0, 1)) == 0)
  }

  test("capacidadFallida: asignacion [0,1,0] del ejemplo 1 → 0 fallos") {
    // M01(25)→E101(30), M02(30)→E102(40), M03(20)→E101(30): todos caben
    assert(capacidadFallida(c1, a1, Vector(0, 1, 0)) == 0)
  }

  test("capacidadFallida: F03(50 est) en S201(cap 45) → 1 fallo") {
    // Solo F03 no cabe; los demás sí
    assert(capacidadFallida(c2, a2, Vector(0, 1, 0, 1)) == 1)
  }

  test("capacidadFallida: F03(50) en S202(cap 30) y F01(40) en S202(cap 30) → 2 fallos") {
    // F01→S202(30<40) falla, F02→S201(45>=25) ok,
    // F03→S202(30<50) falla, F04→S201(45>=15) ok
    assert(capacidadFallida(c2, a2, Vector(1, 0, 1, 0)) == 2)
  }

  test("capacidadFallida: cursos vacíos → 0 fallos") {
    assert(capacidadFallida(Vector.empty, a1, Vector.empty) == 0)
  }

  test("capacidadFallida: todos los cursos fallan capacidad → n fallos") {
    // Aula con cap=1, cursos con 10 est cada uno → todos fallan
    val aulaPequena: Aulas = Vector(("mini", 1))
    val cursosGrandes: Cursos = Vector(
      ("C0", 0, 2, 10), ("C1", 3, 5, 10), ("C2", 6, 8, 10)
    )
    assert(capacidadFallida(cursosGrandes, aulaPequena, Vector(0, 0, 0)) == 3)
  }

  // desperdicio
  test("desperdicio: asignacion [0,0,1] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E101(30)-M02(30)=0, E102(40)-M03(20)=20 → 25
    assert(desperdicio(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicio: asignacion [0,1,0] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E102(40)-M02(30)=10, E101(30)-M03(20)=10 → 25
    assert(desperdicio(c1, a1, Vector(0, 1, 0)) == 25)
  }

  // costoAsignacion
  test("costoAsignacion: asignacion [0,0,1] cuesta 1031") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] cuesta 37") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
  }

  // generarAsignaciones
  test("generarAsignaciones: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignaciones(2, 2).length == 4)
  }

  test("generarAsignaciones: 3 cursos y 3 aulas produce 27 asignaciones") {
    assert(generarAsignaciones(3, 3).length == 27)
  }

  // asignacionOptima
  test("asignacionOptima: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptima(c1, a1, d1, w)
    assert(costo <= 37)
  }
}
