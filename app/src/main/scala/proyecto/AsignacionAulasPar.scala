package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = {
    val n = cursos.length
    val mid = n / 2

    val cursosIzq = cursos.take(mid)
    val asigIzq = a.take(mid)
    val cursosDer = cursos.drop(mid)
    val asigDer = a.drop(mid)

    //Choques cruzados: un curso de izq vs uno de der
    def choquesCruzados(): Int ={
      (for {
        i <- cursosIzq.indices
        j <- cursosDer.indices
        if asigIzq(i) == asigDer(j) && asigIzq(i) >= 0
        if solapan(cursosIzq(i), cursosDer(j))
      } yield 1).sum
    }

    //Ejecurar en paralelo los choques de cada mitad
    val (choquesIzq, choquesDer) = parallel(
      choques(cursosIzq, asigIzq),
      choques(cursosDer, asigDer)
    )

    choquesIzq + choquesDer + choquesCruzados()
  }

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = ???

  /** Versión paralela de movilidad: divide el vector de cursos en dos mitades. */
  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                   a: Asignacion): Int = ???

  /**
   * Versión paralela de generarAsignaciones:
   * paraleliza la construcción usando parallel sobre los valores del primer curso.
   */
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = ???

  /**
   * Versión paralela de asignacionOptima:
   * divide el espacio de candidatos en dos mitades y combina los mínimos.
   */
  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = ???
}
