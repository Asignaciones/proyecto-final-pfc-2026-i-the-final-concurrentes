package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  // Datos del Ejemplo 1
  val c1: Cursos     = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas      = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos       = (1000, 100, 1, 2)

  // Datos del Ejemplo 2
  val c2: Cursos     = Vector(("F01", 0, 4, 40), ("F02", 4, 8, 25), ("F03", 8, 12, 50), ("F04", 12, 16, 15))
  val a2: Aulas      = Vector(("S201", 45), ("S202", 30))
  val d2: Distancias = Vector(Vector(0, 5), Vector(5, 0))
  val w2: Pesos      = (1000, 100, 1, 2)

  // ---------------------------------------------------------------------------
  // choquesPar
  // ---------------------------------------------------------------------------

  test("choquesPar - mismo resultado que choques (ejemplo 1, alpha1)") {
    val asig: Asignacion = Vector(0, 0, 1)
    assert(choquesPar(c1, asig) == choques(c1, asig))
    assert(choquesPar(c1, asig) == 1)
  }

  test("choquesPar - mismo resultado que choques (ejemplo 1, alpha2)") {
    val asig: Asignacion = Vector(0, 1, 0)
    assert(choquesPar(c1, asig) == choques(c1, asig))
    assert(choquesPar(c1, asig) == 0)
  }

  test("choquesPar - detecta choques cruzados entre mitades") {
    val cursos: Cursos   = Vector(
      ("C0", 0, 6, 20), ("C1", 8, 12, 20),
      ("C2", 3, 9, 20), ("C3", 14, 18, 20)
    )
    val asig: Asignacion = Vector(0, 1, 0, 1)
    assert(choquesPar(cursos, asig) == choques(cursos, asig))
  }

  test("choquesPar - multiples choques en la misma aula") {
    val cursos: Cursos   = Vector(
      ("C0", 0, 10, 10), ("C1", 2, 12, 10),
      ("C2", 4, 14, 10), ("C3", 6, 16, 10)
    )
    val asig: Asignacion = Vector(0, 0, 0, 0)
    assert(choquesPar(cursos, asig) == choques(cursos, asig))
    assert(choquesPar(cursos, asig) == 6)
  }

  test("choquesPar - cursos consecutivos en la misma aula no chocan") {
    val cursos: Cursos   = Vector(("C1", 0, 4, 20), ("C2", 4, 8, 20))
    val asig: Asignacion = Vector(0, 0)
    assert(choquesPar(cursos, asig) == 0)
  }

  // ---------------------------------------------------------------------------
  // desperdicioPar
  // ---------------------------------------------------------------------------

  test("desperdicioPar - mismo resultado que desperdicio (ejemplo 1, alpha1)") {
    val asig: Asignacion = Vector(0, 0, 1)
    assert(desperdicioPar(c1, a1, asig) == desperdicio(c1, a1, asig))
    assert(desperdicioPar(c1, a1, asig) == 25)
  }

  test("desperdicioPar - mismo resultado que desperdicio (ejemplo 1, alpha2)") {
    val asig: Asignacion = Vector(0, 1, 0)
    assert(desperdicioPar(c1, a1, asig) == desperdicio(c1, a1, asig))
    assert(desperdicioPar(c1, a1, asig) == 25)
  }

  test("desperdicioPar - mismo resultado que desperdicio (ejemplo 2, alpha1)") {
    val asig: Asignacion = Vector(0, 1, 0, 1)
    assert(desperdicioPar(c2, a2, asig) == desperdicio(c2, a2, asig))
    assert(desperdicioPar(c2, a2, asig) == 25)
  }

  test("desperdicioPar - cursos que no caben no suman desperdicio") {
    val cursos: Cursos   = Vector(("C1", 0, 4, 50), ("C2", 4, 8, 50))
    val aulas: Aulas     = Vector(("E1", 30), ("E2", 30))
    val asig: Asignacion = Vector(0, 1)
    assert(desperdicioPar(cursos, aulas, asig) == desperdicio(cursos, aulas, asig))
    assert(desperdicioPar(cursos, aulas, asig) == 0)
  }

  test("desperdicioPar - capacidad exacta produce desperdicio cero") {
    val cursos: Cursos   = Vector(("C1", 0, 4, 30), ("C2", 4, 8, 40))
    val aulas: Aulas     = Vector(("E1", 30), ("E2", 40))
    val asig: Asignacion = Vector(0, 1)
    assert(desperdicioPar(cursos, aulas, asig) == 0)
  }

  // ---------------------------------------------------------------------------
  // movilidadPar
  // ---------------------------------------------------------------------------

  test("movilidadPar - mismo resultado que movilidad (ejemplo 1, alpha1)") {
    val asig: Asignacion = Vector(0, 0, 1)
    assert(movilidadPar(c1, a1, d1, asig) == movilidad(c1, a1, d1, asig))
    assert(movilidadPar(c1, a1, d1, asig) == 3)
  }

  test("movilidadPar - mismo resultado que movilidad (ejemplo 1, alpha2)") {
    val asig: Asignacion = Vector(0, 1, 0)
    assert(movilidadPar(c1, a1, d1, asig) == movilidad(c1, a1, d1, asig))
    assert(movilidadPar(c1, a1, d1, asig) == 6)
  }

  test("movilidadPar - mismo resultado que movilidad (ejemplo 2, alpha1)") {
    val asig: Asignacion = Vector(0, 1, 0, 1)
    assert(movilidadPar(c2, a2, d2, asig) == movilidad(c2, a2, d2, asig))
    assert(movilidadPar(c2, a2, d2, asig) == 15)
  }

  test("movilidadPar - todos en la misma aula, distancia cero") {
    val cursos: Cursos   = Vector(("C1", 0, 4, 10), ("C2", 4, 8, 10), ("C3", 8, 12, 10))
    val aulas: Aulas     = Vector(("E1", 20), ("E2", 20))
    val d: Distancias    = Vector(Vector(0, 5), Vector(5, 0))
    val asig: Asignacion = Vector(0, 0, 0)
    assert(movilidadPar(cursos, aulas, d, asig) == 0)
  }

  test("movilidadPar - un solo curso asignado, movilidad cero") {
    val cursos: Cursos   = Vector(("C1", 0, 4, 10))
    val aulas: Aulas     = Vector(("E1", 20))
    val d: Distancias    = Vector(Vector(0))
    val asig: Asignacion = Vector(0)
    assert(movilidadPar(cursos, aulas, d, asig) == 0)
  }

  // ---------------------------------------------------------------------------
  // generarAsignacionesPar
  // ---------------------------------------------------------------------------

  test("generarAsignacionesPar - 2 cursos 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  test("generarAsignacionesPar - 3 cursos 2 aulas produce 8 asignaciones") {
    assert(generarAsignacionesPar(3, 2).length == 8)
  }

  test("generarAsignacionesPar - mismo conjunto que version secuencial (2x2)") {
    val par = generarAsignacionesPar(2, 2).toSet
    val seq = generarAsignaciones(2, 2).toSet
    assert(par == seq)
  }

  test("generarAsignacionesPar - mismo conjunto que version secuencial (3x3)") {
    val par = generarAsignacionesPar(3, 3).toSet
    val seq = generarAsignaciones(3, 3).toSet
    assert(par == seq)
  }

  test("generarAsignacionesPar - 0 cursos produce una asignacion vacia") {
    assert(generarAsignacionesPar(0, 3) == Vector(Vector.empty))
  }

  // ---------------------------------------------------------------------------
  // asignacionOptimaPar
  // ---------------------------------------------------------------------------

  test("asignacionOptimaPar - mismo costo que version secuencial (ejemplo 1)") {
    val (_, costoSeq) = asignacionOptima(c1, a1, d1, w)
    val (_, costoPar) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costoPar == costoSeq)
  }

  test("asignacionOptimaPar - costo optimo no supera el de alpha2 (37) en ejemplo 1") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }

  test("asignacionOptimaPar - mismo costo que version secuencial (ejemplo 2)") {
    val (_, costoSeq) = asignacionOptima(c2, a2, d2, w2)

    val (_, costoPar) = asignacionOptimaPar(c2, a2, d2, w2)
    assert(costoPar == costoSeq)
  }

  test("asignacionOptimaPar - la asignacion optima es valida (todos los indices en rango)") {
    val (asig, _) = asignacionOptimaPar(c1, a1, d1, w)
    assert(asig.forall(j => j >= 0 && j < a1.length))
  }

  test("asignacionOptimaPar - longitud de la asignacion igual al numero de cursos") {
    val (asig, _) = asignacionOptimaPar(c1, a1, d1, w)
    assert(asig.length == c1.length)
  }
}