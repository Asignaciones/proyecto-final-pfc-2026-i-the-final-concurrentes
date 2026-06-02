package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  test("choquesPar - mismo resultado que choques version secuencial (ejemplo 1, alpha1)") {
    val asig1: Asignacion = Vector(0, 0, 1)
    assert(choquesPar(c1, asig1) == choques(c1, asig1))
    assert(choquesPar(c1, asig1) == 1)
  }

  test("choquesPar - mismo resultado que choques version secuencial (ejemplo 1, alpha2)") {
    val asig2: Asignacion = Vector(0, 1, 0)
    assert(choquesPar(c1, asig2) == choques(c1, asig2))
    assert(choquesPar(c1, asig2) == 0)
  }

  test("choquesPar - sin solapamientos devuelve 0") {
    val cursos: Cursos = Vector(
      ("C1", 4, 8, 20),
      ("C2", 8, 12, 20)
    )
    assert(choquesPar(cursos, Vector(0, 0)) == 0)
  }

  test("choquesPar - detecta choques cruzados entre mitades") {
    val cursos: Cursos = Vector(
      ("C0", 0, 6, 20),
      ("C1", 8, 12, 20),
      ("C2", 3, 9, 20),
      ("C3", 14, 18, 20)
    )
    val asig: Asignacion = Vector(0, 1, 0, 1)
    assert(choquesPar(cursos, asig) == choques(cursos, asig))
  }

  test("choquesPar - multiples choques misma aula") {
    val cursos: Cursos = Vector(
      ("C0", 0, 10, 10),
      ("C1", 2, 12, 10),
      ("C2", 4, 14, 10),
      ("C3", 6, 16, 10)
    )
    assert(choquesPar(cursos, Vector(0, 0, 0, 0)) == 6)
  }

  test("desperdicioPar: asignacion [0,0,1] tiene desperdicio 25") {
    assert(desperdicioPar(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("movilidadPar: asignacion [0,0,1] tiene movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }

  test("generarAsignacionesPar: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  test("asignacionOptimaPar: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }
}
