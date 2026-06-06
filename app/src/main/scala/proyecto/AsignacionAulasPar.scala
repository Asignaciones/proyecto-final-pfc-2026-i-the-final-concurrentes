package proyecto
import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = {
    val n = cursos.length
    val mid = n / 2
    val cursosIzq = cursos.take(mid)
    val asigIzq   = a.take(mid)
    val cursosDer = cursos.drop(mid)
    val asigDer   = a.drop(mid)

    def choquesCruzados(): Int =
      (for {
        i <- cursosIzq.indices
        j <- cursosDer.indices
        if asigIzq(i) >= 0 && asigIzq(i) == asigDer(j)
        if solapan(cursosIzq(i), cursosDer(j))
      } yield 1).sum

    val (choquesIzq, choquesDer) = parallel(
      choques(cursosIzq, asigIzq),
      choques(cursosDer, asigDer)
    )
    choquesIzq + choquesDer + choquesCruzados()
  }

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
    val mid       = cursos.length / 2
    val cursosIzq = cursos.take(mid)
    val asigIzq   = a.take(mid)
    val cursosDer = cursos.drop(mid)
    val asigDer   = a.drop(mid)

    // El desperdicio es una suma independiente por curso → se puede
    // calcular cada mitad en paralelo y luego sumar los resultados.
    val (despIzq, despDer) = parallel(
      desperdicio(cursosIzq, aulas, asigIzq),
      desperdicio(cursosDer, aulas, asigDer)
    )
    despIzq + despDer
  }

  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                   a: Asignacion): Int = {

    // Índices ordenados por hora de inicio (solo cursos asignados)
    val ordenados =
      cursos.indices
        .filter(i => a(i) >= 0)
        .sortBy(i => iniCurso(cursos(i)))
        .toVector

    if (ordenados.length < 2) 0
    else {
      val mid   = ordenados.length / 2
      val izq   = ordenados.take(mid)   // índices en la mitad izquierda
      val der   = ordenados.drop(mid)   // índices en la mitad derecha

      // Suma de distancias dentro de cada mitad (sobre pares consecutivos)
      def sumaDistancias(indices: Vector[Int]): Int =
        indices.sliding(2).foldLeft(0) {
          case (acc, Vector(i, j)) => acc + d(a(i))(a(j))
          case (acc, _)            => acc
        }

      val (movIzq, movDer) = parallel(
        sumaDistancias(izq),
        sumaDistancias(der)
      )

      // Distancia cruzada entre el último de izq y el primero de der
      val distCruzada = d(a(izq.last))(a(der.head))

      movIzq + movDer + distCruzada
    }
  }

  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {
    if (n == 0) Vector(Vector.empty)
    else {
      val mid = m / 2

      // Sub-asignaciones para los (n-1) cursos restantes (secuencial)
      val subAsignaciones = generarAsignaciones(n - 1, m)

      def expandir(aulas: Range): Vector[Asignacion] =
        aulas.toVector.flatMap { aula =>
          subAsignaciones.map(sub => aula +: sub)
        }

      val (izq, der) = parallel(
        expandir(0 until mid),
        expandir(mid until m)
      )
      izq ++ der
    }
  }

  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = {

    val todas = generarAsignacionesPar(cursos.length, aulas.length)
    val mid   = todas.length / 2
    val izq   = todas.take(mid)
    val der   = todas.drop(mid)

    def minimoLocal(candidatas: Vector[Asignacion]): (Asignacion, Int) =
      candidatas
        .map(a => (a, costoAsignacion(cursos, aulas, d, a, w)))
        .minBy(_._2)

    val (minIzq, minDer) = parallel(
      minimoLocal(izq),
      minimoLocal(der)
    )

    // Combinar: quedarse con el de menor costo
    if (minIzq._2 <= minDer._2) minIzq else minDer
  }
}