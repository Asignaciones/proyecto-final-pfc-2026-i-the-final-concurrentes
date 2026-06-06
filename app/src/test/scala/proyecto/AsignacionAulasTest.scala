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
  // nuevos casos de solapan
  test("solapan - un curso contenido completamente dentro de otro") {

    val c1: Curso = ("A", 2, 12, 20)
    val c2: Curso = ("B", 5, 8, 15)

    assert(solapan(c1, c2))
  }
  test("solapan - cursos con exactamente el mismo horario") {

    val c1: Curso = ("A", 4, 10, 20)
    val c2: Curso = ("B", 4, 10, 30)

    assert(solapan(c1, c2))
  }
  test("solapan - cursos separados por un bloque no se solapan") {

    val c1: Curso = ("A", 0, 4, 10)
    val c2: Curso = ("B", 5, 8, 10)

    assert(!solapan(c1, c2))
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
  // nuevos casos de choques
  test("choques - todos los cursos chocan en una misma aula") {

    val cursos: Cursos = Vector(
      ("C1",0,10,10),
      ("C2",1,11,10),
      ("C3",2,12,10),
      ("C4",3,13,10)
    )

    val a: Asignacion = Vector(0,0,0,0)

    assert(choques(cursos,a) == 6)
  }
  test("choques - cursos simultaneos en aulas distintas") {

    val cursos: Cursos = Vector(
      ("C1",0,10,10),
      ("C2",0,10,10)
    )

    assert(choques(cursos, Vector(0,1)) == 0)
  }
  test("choques - ignora cursos sin asignar") {

    val cursos: Cursos = Vector(
      ("C1",0,10,10),
      ("C2",2,8,10)
    )

    assert(choques(cursos, Vector(-1,0)) == 0)
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
  // nuevos casos de capacidadFallida
  test("capacidadFallida - todas las asignaciones fallan") {

    val cursos: Cursos = Vector(
      ("C1",0,4,50),
      ("C2",4,8,60),
      ("C3",8,12,70)
    )

    val aulas: Aulas = Vector(
      ("A1",20),
      ("A2",30)
    )

    assert(capacidadFallida(cursos,aulas,Vector(0,1,0)) == 3)
  }
  test("capacidadFallida - capacidad exacta no cuenta como falla") {

    val cursos: Cursos =
      Vector(("C1",0,4,30))

    val aulas: Aulas =
      Vector(("A1",30))

    assert(capacidadFallida(cursos,aulas,Vector(0)) == 0)
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
  // nuevos casos de desperdicio
  test("desperdicio - capacidad exacta genera desperdicio cero") {

    val cursos: Cursos =
      Vector(("C1",0,4,25))

    val aulas: Aulas =
      Vector(("A1",25))

    assert(desperdicio(cursos,aulas,Vector(0)) == 0)
  }
  test("desperdicio - aula insuficiente no aporta desperdicio") {

    val cursos: Cursos =
      Vector(("C1",0,4,40))

    val aulas: Aulas =
      Vector(("A1",30))

    assert(desperdicio(cursos,aulas,Vector(0)) == 0)
  }
  //Movilidad
  test("movilidad - un unico curso tiene movilidad cero") {

    val cursos: Cursos =
      Vector(("C1",0,4,20))

    val aulas: Aulas =
      Vector(("A1",30))

    val dist: Distancias =
      Vector(Vector(0))

    assert(movilidad(cursos,aulas,dist,Vector(0)) == 0)
  }

  test("movilidad - misma aula produce movilidad cero") {

    val cursos: Cursos = Vector(
      ("C1",0,4,20),
      ("C2",5,8,20),
      ("C3",10,14,20)
    )

    val aulas: Aulas =
      Vector(("A1",40))

    val dist: Distancias =
      Vector(Vector(0))

    assert(movilidad(cursos,aulas,dist,Vector(0,0,0)) == 0)
  }
  test("movilidad - ordena cursos por hora de inicio") {

    val cursos: Cursos = Vector(
      ("C1",10,12,20),
      ("C2",0,4,20),
      ("C3",5,8,20)
    )

    val aulas: Aulas = Vector(
      ("A1",30),
      ("A2",30)
    )

    val dist: Distancias =
      Vector(
        Vector(0,5),
        Vector(5,0)
      )

    val a = Vector(0,0,1)

    assert(movilidad(cursos,aulas,dist,a) == 10)
  }

  // costoAsignacion
  test("costoAsignacion: asignacion [0,0,1] cuesta 1031") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] cuesta 37") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
  }

  // generarAsignaciones
  test("generarAsignaciones - caso base: 0 cursos produce una asignacion vacia") {
    val resultado = generarAsignaciones(0, 3)
    assert(resultado == Vector(Vector.empty))
  }

  test("generarAsignaciones - 1 curso y 3 aulas produce 3 asignaciones") {
    val resultado = generarAsignaciones(1, 3)
    assert(resultado.length == 3)
    assert(resultado.contains(Vector(0)))
    assert(resultado.contains(Vector(1)))
    assert(resultado.contains(Vector(2)))
  }

  test("generarAsignaciones - 2 cursos y 2 aulas produce 4 asignaciones (2^2)") {
    val resultado = generarAsignaciones(2, 2)
    assert(resultado.length == 4)
    assert(resultado.contains(Vector(0, 0)))
    assert(resultado.contains(Vector(0, 1)))
    assert(resultado.contains(Vector(1, 0)))
    assert(resultado.contains(Vector(1, 1)))
  }

  test("generarAsignaciones - 3 cursos y 2 aulas produce 8 asignaciones (2^3)") {
    val resultado = generarAsignaciones(3, 2)
    assert(resultado.length == 8)
  }

  test("generarAsignaciones - 2 cursos y 3 aulas produce 9 asignaciones (3^2)") {
    val resultado = generarAsignaciones(2, 3)
    assert(resultado.length == 9)
    // Verifica que todas las combinaciones estan presentes
    val esperadas = for {
      a <- 0 until 3
      b <- 0 until 3
    } yield Vector(a, b)
    esperadas.foreach { e => assert(resultado.contains(e)) }
  }

  test("generarAsignaciones - todas las asignaciones tienen longitud n") {
    val n = 4
    val m = 3
    val resultado = generarAsignaciones(n, m)
    assert(resultado.forall(_.length == n))
  }

  test("generarAsignaciones - los valores estan en rango [0, m-1]") {
    val m = 3
    val resultado = generarAsignaciones(3, m)
    assert(resultado.forall(_.forall(v => v >= 0 && v < m)))
  }
  // nuevos casos de generarAsignaciones
  test("generarAsignaciones - una sola aula genera una unica asignacion") {

    val resultado = generarAsignaciones(4,1)

    assert(resultado.length == 1)
    assert(resultado.head == Vector(0,0,0,0))
  }
  test("generarAsignaciones - 3 aulas y 4 cursos produce 81 asignaciones") {

    val resultado = generarAsignaciones(4,3)

    assert(resultado.length == 81)
  }

  // asignacionOptima

  // creo que la habia puesto aguirre
//  test("asignacionOptima - ejemplo 1: el costo optimo es 37") {
//    val (asig, costo) = asignacionOptima(c1, a1, d1, w)
//    assert(costo == 37)
//    assert(asig == Vector(0, 1, 0))
//  }
  // nueva
  test("asignacionOptima - devuelve una asignacion de costo minimo") {

    val (asig, costo) = asignacionOptima(c1, a1, d1, w)

    val costoMinimo =
      generarAsignaciones(c1.length, a1.length)
        .map(a => costoAsignacion(c1, a1, d1, a, w))
        .min

    assert(costo == costoMinimo)
  }

  test("asignacionOptima - la asignacion optima no tiene choques") {
    val (asig, _) = asignacionOptima(c1, a1, d1, w)
    assert(choques(c1, asig) == 0)
  }

  test("asignacionOptima - la asignacion resultante tiene longitud igual al numero de cursos") {
    val (asig, _) = asignacionOptima(c1, a1, d1, w)
    assert(asig.length == c1.length)
  }

  test("asignacionOptima - todos los cursos quedan asignados (sin -1)") {
    val (asig, _) = asignacionOptima(c1, a1, d1, w)
    assert(asig.forall(_ >= 0))
  }

  test("asignacionOptima - prefiere evitar choques sobre minimizar desperdicio") {
    val cursos: Cursos = Vector(("C0", 0, 6, 10), ("C1", 3, 9, 10))
    val aulas: Aulas = Vector(("A0", 50), ("A1", 50))
    val dist: Distancias = Vector(Vector(0, 1), Vector(1, 0))
    val (asig, _) = asignacionOptima(cursos, aulas, dist, w)
    assert(asig(0) != asig(1))
  }

  // asignacionOptima - nuevo caso
  test("asignacionOptima - un curso y un aula") {

    val cursos: Cursos =
      Vector(("C1",0,4,20))

    val aulas: Aulas =
      Vector(("A1",30))

    val dist: Distancias =
      Vector(Vector(0))

    val pesos: Pesos =
      (1000,100,1,2)

    val (a,costo) =
      asignacionOptima(cursos,aulas,dist,pesos)

    assert(a == Vector(0))
    assert(costo == 10)
  }
}
