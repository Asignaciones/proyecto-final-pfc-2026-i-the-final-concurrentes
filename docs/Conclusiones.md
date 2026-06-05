# Conclusiones

**Integrantes:** [completar]

---

## Conclusiones del proyecto

Presente aquí las conclusiones del proyecto. Como mínimo debe responder:

1. **Programación funcional:** ¿Qué ventajas y dificultades encontraron al implementar
   la solución usando recursión y funciones de alto orden en lugar de ciclos iterativos?

2. **Corrección:** ¿Cómo argumentaron formalmente que sus implementaciones son correctas?
   ¿Qué técnicas de inducción estructural o de invariantes aplicaron?

3. **Paralelismo:** ¿En qué escenarios resultó beneficioso paralelizar? ¿Cuándo la
   sobrecarga del sistema superó la ganancia esperada?

4. **Aprendizajes:** ¿Qué conceptos del curso les resultaron más útiles para resolver
   el problema? ¿Qué cambiarían en su diseño si volvieran a empezar?

---

**Integrantes:** 
[  Oliver De Jesus Arboleda Baez,
   Jose David Jaramillo Rebellon,
   Juan Esteban Aguirre Castañeda,
   Jhoan Fabricio Hurtado Marin 
]

---

## Conclusiones del proyecto

### 1. Programación funcional

La implementación en estilo funcional, usando recursión y funciones de alto orden, presentó ventajas y dificultades concretas a lo largo del proyecto.

Entre las **ventajas**, la más notable fue que la estructura recursiva de funciones como `generarAsignaciones` facilitó enormemente la demostración formal de corrección mediante inducción estructural: el caso base y el paso inductivo se correspondieron directamente con las ramas del código. Además, el uso de funciones como `flatMap`, `map` y `minBy` permitió expresar operaciones complejas de forma concisa y declarativa, reduciendo la posibilidad de errores por manejo manual de índices o estados mutables.

Entre las **dificultades**, la ausencia de ciclos iterativos exigió un cambio de mentalidad importante, especialmente para razonar sobre el rendimiento: en un ciclo es intuitivo ver cuántas iteraciones ocurren, mientras que en la recursión esto requiere análisis más cuidadoso. También fue necesario familiarizarse con el comportamiento de colecciones inmutables en Scala, como `Vector`, y entender cuándo operaciones como `take`, `drop` o `indices` generan copias nuevas.

### 2. Corrección

La argumentación formal de corrección se apoyó principalmente en dos técnicas:

- **Inducción estructural sobre `n`**: aplicada a `generarAsignaciones`, donde se demostró que el caso base ($n = 0$) es correcto, y que si la función es correcta para $n-1$ cursos, también lo es para $n$. Esto garantizó que se generan exactamente $m^n$ asignaciones sin repeticiones ni omisiones.

- **Partición exhaustiva y disjunta**: aplicada a `choquesPar`, donde se demostró que todo par de cursos $(i, j)$ con $i < j$ pertenece a exactamente una de tres categorías (izquierda-izquierda, cruzados, derecha-derecha), y que cada categoría es calculada correctamente por la función correspondiente. La exhaustividad y disjunción de la partición garantizan que la suma de los tres resultados equivale al total de choques.

- **Argumento de completitud y selección**: aplicado a `asignacionOptima`, donde se combinó la corrección de `generarAsignaciones` (ningún candidato omitido) con el comportamiento de `minBy` (selección del mínimo en una colección no vacía) para concluir que la función retorna un óptimo global garantizado.

### 3. Paralelismo

La paralelización resultó beneficiosa principalmente cuando el número de cursos era suficientemente grande. En `choquesPar`, dividir el problema en dos mitades y calcular los choques internos de cada mitad con `parallel` redujo el tiempo de ejecución en entradas grandes, ya que ambas mitades pueden procesarse simultáneamente en núcleos distintos.

Sin embargo, se identificaron escenarios donde la sobrecarga del sistema superó la ganancia esperada: con pocos cursos (por ejemplo, 3 o 4), el costo de crear y sincronizar hilos mediante `parallel` resultó comparable o mayor al tiempo de cálculo secuencial. Esto evidencia que la paralelización no es universalmente beneficiosa y debe evaluarse según el tamaño de la entrada y el costo real de cada subproblema.

En términos asintóticos, la complejidad de `choquesPar` sigue siendo $O(n^2)$ debido al cálculo secuencial de los choques cruzados, lo que limita la ganancia teórica de la paralelización.

### 4. Aprendizajes

Los conceptos del curso que resultaron más útiles para resolver el problema fueron:

- **Inducción estructural**, que permitió demostrar corrección de funciones recursivas de forma rigurosa y sistemática.
- **Funciones de alto orden** (`map`, `flatMap`, `minBy`, `filter`), que facilitaron expresar la lógica de búsqueda y evaluación de asignaciones de forma clara y sin efectos secundarios.
- **Paralelismo con `parallel`**, que permitió dividir trabajo independiente entre hilos de forma transparente, sin necesidad de manejo explícito de sincronización en los casos donde las mitades no comparten datos.
- **Pruebas unitarias con ScalaTest**, que fueron fundamentales para validar el comportamiento de cada función de forma incremental y detectar errores antes de integrar los componentes.

Si pudiéramos volver a empezar, consideraríamos dos cambios principales en el diseño: primero, definir desde el inicio una suite de pruebas más completa antes de implementar (enfoque *test-driven*), lo que habría agilizado la detección de errores en funciones como `choques` y `desperdicio`. Segundo, evaluar con más cuidado el umbral de tamaño a partir del cual vale la pena paralelizar, para no introducir sobrecarga innecesaria en entradas pequeñas.

---
