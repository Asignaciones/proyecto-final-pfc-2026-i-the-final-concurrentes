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
 
### Conclusión
 
`choquesPar` es **correcta**: particiona los pares en tres grupos disjuntos y exhaustivos, calcula cada grupo correctamente, y suma los tres resultados.
 
---
 
## 4. Casos de prueba
 
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
