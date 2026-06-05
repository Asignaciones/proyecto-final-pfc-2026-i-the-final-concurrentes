# Informe de Corrección

---

## Corrección de `generarAsignaciones`

### Especificación formal

Sea $G(n, m)$ el conjunto de todos los vectores de longitud $n$ con valores en $\{0, \ldots, m-1\}$:

$$G(n, m) = \{ \alpha \in \{0,\ldots,m-1\}^n \}$$

La función debe cumplir: `generarAsignaciones(n, m).toSet == G(n, m)` y `generarAsignaciones(n, m).length == m^n`.

### Argumentación por inducción sobre `n`

**Caso base** ($n = 0$):

La única asignación para 0 cursos es el vector vacío $\langle \rangle$. La función retorna `Vector(Vector.empty)`, que es exactamente $G(0, m) = \{\langle\rangle\}$. ✓

**Hipótesis inductiva:** Supongamos que `generarAsignaciones(n-1, m)` genera correctamente todos los vectores de $G(n-1, m)$, con $m^{n-1}$ elementos.

**Paso inductivo** ($n > 0$):

La función construye:

$$\bigcup_{j=0}^{m-1} \{ j \cdot \alpha \mid \alpha \in G(n-1, m) \}$$

donde $j \cdot \alpha$ denota anteponer $j$ al vector $\alpha$. Esta unión produce exactamente $G(n, m)$, ya que todo vector de longitud $n$ sobre $\{0,\ldots,m-1\}$ empieza con algún $j \in \{0,\ldots,m-1\}$, y el sufijo de longitud $n-1$ está cubierto por la hipótesis inductiva. El tamaño es $m \cdot m^{n-1} = m^n$. ✓

**Sin duplicados:** los conjuntos $\{j\} \times G(n-1,m)$ son disjuntos para distintos $j$, por lo que no hay asignaciones repetidas. ✓

### Ejemplo ilustrativo

Para $n = 2$, $m = 2$:

| Paso | Asignaciones generadas |
|------|------------------------|
| $n=0$ | $\{\langle\rangle\}$ |
| $n=1$ | $\{\langle 0\rangle, \langle 1\rangle\}$ |
| $n=2$ | $\{\langle 0,0\rangle, \langle 0,1\rangle, \langle 1,0\rangle, \langle 1,1\rangle\}$ |

Total: $2^2 = 4$ asignaciones. ✓

### Complejidad

Para cada uno de los $n$ cursos se generan $m$ posibilidades de asignación. El número total de asignaciones generadas es:

$$m^n$$

Por lo tanto, el tiempo de ejecución es:

$$T(n) = O(m^n)$$

y el espacio requerido también es:

$$O(m^n)$$

porque todas las asignaciones se almacenan en memoria.

### Conclusión

`generarAsignaciones` es **correcta**: genera exactamente todos los vectores en $G(n, m)$ sin repeticiones ni omisiones.

---

## Corrección de `asignacionOptima`

### Especificación formal

Dados $C$, $A$, $D_A$, $w$, la función debe retornar:

$$\alpha^* = \arg\min_{\alpha \in G(n,m)} CT^\alpha_{C,A,D_A}$$

junto con su costo $CT^{\alpha^*}_{C,A,D_A}$.

### Argumentación

La implementación tiene tres pasos:

**Paso 1 — Generación completa:**

Por la corrección de `generarAsignaciones` (demostrada arriba), `generarAsignaciones(n, m)` produce **todas** las asignaciones posibles $G(n, m)$. No se omite ningún candidato.

**Paso 2 — Evaluación correcta:**

Para cada $\alpha \in G(n, m)$, `costoAsignacion(cursos, aulas, d, a, w)` calcula (por corrección de la función de un compañero):

$$CT^\alpha_{C,A,D_A} = w_{CH} \cdot CH^\alpha_C + w_{CF} \cdot CF^\alpha_{C,A} + w_{DE} \cdot DE^\alpha_{C,A} + w_{MV} \cdot MV^\alpha_{C,A,D_A}$$

**Paso 3 — Selección del mínimo:**

`minBy` sobre una colección no vacía (garantizada porque $m \geq 1, n \geq 0$ y $G$ tiene al menos $m^0 = 1$ elemento) selecciona el par $(\alpha, CT)$ con el menor $CT$. Por definición de `minBy` en Scala, esto es equivalente a:

$$\arg\min_{\alpha \in G(n,m)} CT^\alpha_{C,A,D_A}$$

### Caso especial: múltiples óptimos

Si varios vectores tienen el mismo costo mínimo, `minBy` retorna el primero encontrado. Esto es válido pues cualquier óptimo satisface la especificación.

### Ejemplo ilustrativo

Con los datos del enunciado:

| Asignación | Costo |
|------------|-------|
| `Vector(0, 0, 0)` | alto (choques) |
| `Vector(0, 1, 0)` | 37 |
| `Vector(1, 0, 0)` | 31 |
| ... | ... |

```
Resultado: (Vector(1,0,0), 31)
```

### Complejidad

La función evalúa todas las asignaciones posibles generadas por `generarAsignaciones`. Si existen $m^n$ asignaciones posibles y el cálculo del costo para una asignación toma tiempo lineal respecto al número de cursos, entonces:

$$T(n) = O(m^n \cdot n)$$

La complejidad está dominada por la generación y evaluación de todas las asignaciones posibles.

### Conclusión

`asignacionOptima` es **correcta**: revisa todos los candidatos posibles y retorna uno con costo mínimo garantizado.

---

## Corrección de `choquesPar`

### Especificación formal

`choquesPar` debe calcular el mismo valor que `choques`:

$$CH^\alpha_C = |\{(i,j) \mid 0 \le i < j < n,\ \alpha_i = \alpha_j \ge 0,\ c_i \text{ solapa con } c_j\}|$$

### Argumentación

Sea $n = |C|$ y $mid = \lfloor n/2 \rfloor$. Se definen:

- $I = \{0, \ldots, mid-1\}$ (índices izquierdos)
- $D = \{mid, \ldots, n-1\}$ (índices derechos)

Todo par $(i, j)$ con $i < j$ pertenece a exactamente una de tres categorías:

$$\{(i,j) \mid i < j\} = \underbrace{\{(i,j) \mid i,j \in I,\ i < j\}}_{\text{izq-izq}} \cup \underbrace{\{(i,j) \mid i \in I,\ j \in D\}}_{\text{cruzados}} \cup \underbrace{\{(i,j) \mid i,j \in D,\ i < j\}}_{\text{der-der}}$$

Estas tres categorías son **disjuntas** y su unión es exacta. Entonces:

$$CH^\alpha_C = CH^{\alpha_I}_{C_I} + CH^{\alpha_D}_{C_D} + CH^{cruzados}$$

donde:
- $CH^{\alpha_I}_{C_I}$ es calculado por `choques(cursosIzq, asigIzq)` ✓
- $CH^{\alpha_D}_{C_D}$ es calculado por `choques(cursosDer, asigDer)` ✓
- $CH^{cruzados}$ es calculado por `choquesCruzados()`, que recorre todos los pares $(i, j)$ con $i \in I, j \in D$ y cuenta los que comparten aula y se solapan ✓

Los dos primeros se ejecutan en paralelo con `parallel`, lo que no altera su resultado (solo mejora el tiempo de ejecución).

**Corrección de `choquesCruzados`:**

La comprensión de lista itera todos los pares $(i, j)$ con $i \in \{0,\ldots,|I|-1\}$ y $j \in \{0,\ldots,|D|-1\}$. Las guardas verifican:
1. $\alpha_I(i) = \alpha_D(j)$ y ambos $\geq 0$ (misma aula, asignados)
2. `solapan(cursosIzq(i), cursosDer(j))` (traslape temporal)

Esto corresponde exactamente a la definición de choque para pares cruzados. ✓

### Ejemplo ilustrativo

Con `cursos = Vector(("C0",0,6,20), ("C1",8,12,20), ("C2",3,9,20), ("C3",14,18,20))` y `asig = Vector(0,1,0,1)`:

| Par | Mitad | Misma aula | Solapan | Choque |
|-----|-------|------------|---------|--------|
| (C0, C2) | cruzado | ✓ (aula 0) | ✓ | 1 |
| (C1, C3) | cruzado | ✓ (aula 1) | ✗ | 0 |

Resultado: `choquesPar == choques == 1` ✓

### Complejidad

La función divide el problema en dos mitades y calcula los choques internos de cada mitad en paralelo. Posteriormente calcula los choques cruzados entre ambas mitades. La relación de recurrencia es:

$$T(n) = 2T\left(\frac{n}{2}\right) + O(n^2)$$

debido al cálculo de los choques cruzados. Aplicando el Teorema Maestro:

$$T(n) = O(n^2)$$

La paralelización reduce el tiempo de ejecución práctico, aunque la complejidad asintótica sigue siendo cuadrática.

### Conclusión

`choquesPar` es **correcta**: particiona los pares en tres grupos disjuntos y exhaustivos, calcula cada grupo correctamente, y suma los tres resultados.

---

## Casos de prueba

Los casos de prueba están implementados en `src/test/scala/proyecto/MisPruebasTest.scala` y se ejecutan con:

```bash
./gradlew test
```

### Resumen de casos por función

| Función | Casos | Descripción |
|---------|-------|-------------|
| `generarAsignaciones` | 7 | Caso base, tamaños 1/2/3/4, rangos válidos, longitudes correctas |
| `asignacionOptima` | 6 | Ejemplos del enunciado, propiedades de la solución, preferencia por no-choques |
| `choquesPar` | 6 | Sin solapamientos, igualdad con secuencial, choques cruzados, casos múltiples |

### Descripción detallada de casos — `generarAsignaciones`

| # | Entrada | Resultado esperado | Propiedad verificada |
|---|---------|-------------------|----------------------|
| 1 | `n=0, m=2` | `Vector(Vector())` — longitud 1 | Caso base |
| 2 | `n=1, m=2` | Longitud 2 | Un curso, dos aulas |
| 3 | `n=2, m=2` | Longitud 4 | $2^2$ asignaciones |
| 4 | `n=3, m=2` | Longitud 8 | $2^3$ asignaciones |
| 5 | `n=2, m=3` | Longitud 9 | $3^2$ asignaciones |
| 6 | `n=3, m=2` | Valores en $\{0,1\}$ | Rango correcto |
| 7 | `n=2, m=2` | Sin duplicados | `toSet.size == length` |

### Descripción detallada de casos — `choquesPar`

| # | Descripción | Resultado esperado |
|---|-------------|-------------------|
| 1 | Cursos sin solapamiento, misma aula | 0 choques |
| 2 | `asig = Vector(0,0,1)` vs secuencial | Iguales, valor 1 |
| 3 | `asig = Vector(0,1,0)` vs secuencial | Iguales, valor 0 |
| 4 | Choque cruzado entre mitades | Igual a secuencial |
| 5 | Cuatro cursos todos solapados misma aula | 6 choques |
| 6 | Aulas distintas, sin solapamiento cruzado | 0 choques |

---

## Decisiones de Diseño

- Se utilizó **recursión** en lugar de ciclos iterativos para cumplir las restricciones funcionales del proyecto.
- Se emplearon **funciones de alto orden** (`map`, `flatMap`, `minBy`) para mantener un estilo funcional idiomático en Scala.
- La generación de asignaciones se implementó mediante **construcción recursiva de vectores**, garantizando cobertura completa del espacio de búsqueda.
- La versión paralela de `choques` **divide el problema en dos mitades** para aprovechar concurrencia mediante `parallel`, sin alterar la corrección del resultado.
- Los choques cruzados se calculan de forma **secuencial después de la barrera de sincronización**, ya que dependen de resultados de ambas mitades.
- Se priorizó la **legibilidad y verificabilidad** del código sobre optimizaciones prematuras, facilitando la demostración formal de corrección.