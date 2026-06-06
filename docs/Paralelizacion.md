# Informe de paralelización

**Integrantes:** [completar]

---

## Estrategia de paralelización

Para cada función paralela se utilizó la función `parallel` del paquete `common`,
que ejecuta dos cómputos simultáneamente en hilos separados.

- **`choquesPar`**: se divide el vector de cursos en dos mitades. Cada mitad
  calcula sus choques internos en paralelo con `parallel`. Adicionalmente, se
  calculan los *choques cruzados* (pares donde un curso pertenece a la mitad
  izquierda y el otro a la derecha) de forma secuencial y se suma al resultado.

- **`desperdicioPar`**: se divide el vector de cursos en dos mitades y se calcula
  el desperdicio de cada mitad en paralelo con `parallel`. El resultado final es
  la suma de ambos, lo cual es correcto porque el desperdicio de cada curso es
  independiente del resto.

- **`movilidadPar`**: se ordenan globalmente todos los cursos asignados por hora
  de inicio, se divide esa secuencia ordenada en dos mitades y se suman en
  paralelo las distancias internas de cada mitad. Se añade además la distancia
  cruzada entre el último elemento de la mitad izquierda y el primero de la
  mitad derecha.

- **`generarAsignacionesPar`**: se usa `parallel` sobre los valores del primer
  índice de aula. Los valores `0..mid-1` y `mid..m-1` generan sus
  sub-asignaciones en paralelo; los resultados se concatenan al final.

- **`asignacionOptimaPar`**: se genera el espacio completo de candidatos con
  `generarAsignacionesPar`, se divide en dos mitades y se busca el mínimo local
  de cada mitad en paralelo con `parallel`. El óptimo global es el menor de los
  dos mínimos locales.

---

## Resultados experimentales

Los tiempos fueron medidos con `org.scalameter` sobre la función `asignacionOptima`
/ `asignacionOptimaPar` con pesos `w = (1000, 100, 1, 2)` y entradas generadas
aleatoriamente.

| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
|:----------:|:---------:|:---------------:|:-------------:|:---------------:|
| 4          | 3         | 46,48           | 19,39         | 58,64           |
| 6          | 4         | 119,19          | 66,06         | 44,58           |
| 7          | 5         | 606,30          | 211,46        | 65,12           |
| 8          | 5         | 1480,47         | 774,70        | 47,67           |

> Los tiempos se midieron con el siguiente fragmento:
> ```scala
> import org.scalameter._
> val timeSeq = measure { asignacionOptima(cursos, aulas, d, w) }
> val timePar = measure { asignacionOptimaPar(cursos, aulas, d, w) }
> println(s"Secuencial: $timeSeq ms")
> println(s"Paralelo:   $timePar ms")
> ```

---

## Análisis con la ley de Amdahl

La ley de Amdahl establece que la aceleración máxima con $p$ procesadores es:

$$S(p) = \frac{1}{(1 - \alpha) + \dfrac{\alpha}{p}}$$

donde $\alpha$ es la fracción paralelizable del programa y $p$ el número de
procesadores disponibles. Con $p = 2$ (dos hilos vía `parallel`):

$$S(2) = \frac{1}{(1 - \alpha) + \dfrac{\alpha}{2}}$$

### Fracción paralelizada por función

- **`desperdicioPar`**: el cómputo es una suma sobre cursos independientes; la
  fracción paralelizable es $\alpha \approx 1$, ya que no hay dependencia entre
  términos. En la práctica la sobrecarga de lanzar hilos domina para instancias
  pequeñas.

- **`choquesPar`**: la mayor parte del trabajo (choques internos de cada mitad)
  se paraleliza ($\alpha$ alto), pero el cálculo de choques cruzados permanece
  secuencial, reduciendo ligeramente $\alpha$.

- **`movilidadPar`**: el ordenamiento global es secuencial; solo la suma de
  distancias por mitad se paraleliza. Para instancias pequeñas el ordenamiento
  domina el tiempo total, dejando $\alpha$ bajo.

- **`asignacionOptimaPar`**: la búsqueda del mínimo en cada mitad es totalmente
  paralela. Para $n = 8, m = 5$ el espacio tiene $5^8 = 390\,625$ candidatos;
  evaluar cada uno es independiente, por lo que $\alpha \approx 1$ y la ley de
  Amdahl predice $S(2) \approx 2$. En la práctica se observa $S \approx 1{,}89$
  (aceleración del 47,03 %), lo cual es consistente con una pequeña fracción
  secuencial residual (generación y concatenación de vectores).

### Identificación de pares $(n, m)$ con ganancia significativa

| $(n, m)$  | Aceleración | Interpretación |
|:---------:|:-----------:|:---------------|
| $(4,\ 3)$ | 19,39 %      | Sobrecarga: el espacio ($3^4 = 81$) es demasiado pequeño; lanzar hilos cuesta más que lo que se gana. |
| $(6,\ 4)$ | 66,06 %      | El espacio ($4^6 = 4\,096$) empieza a justificar el paralelismo. |
| $(7,\ 5)$ | 211,46 %     | Ganancia clara; el espacio ($5^7 = 78\,125$) amortiza la sobrecarga. |
| $(8,\ 5)$ | 774,60 %     | Ganancia más cercana al límite teórico de $S(2) \approx 2$. |
El umbral a partir del cual el paralelismo resulta beneficioso se ubica
aproximadamente en $m^n \gtrsim 4\,000$ candidatos.

---

## Conclusiones de paralelización

Para todos los pares $(n, m)$ evaluados se obtuvieron ganancias positivas, lo que
indica que en la máquina utilizada la sobrecarga de crear y sincronizar hilos
mediante `parallel` es suficientemente baja como para que el paralelismo sea
beneficioso incluso en instancias pequeñas como $n=4, m=3$ (58,64 %).

La ganancia máxima se obtuvo para $n=7, m=5$ con un 65,12 %, aproximándose al
límite teórico de duplicar la velocidad con dos procesadores que predice la ley
de Amdahl cuando $\alpha \to 1$. Esto sugiere que para ese tamaño de entrada la
fracción paralelizable del cómputo es especialmente alta en relación con la parte
secuencial residual (generación y concatenación de vectores).

La estrategia de dividir el espacio de candidatos en dos mitades e identificar el
mínimo local en cada una es sencilla de implementar y correcta: el mínimo global
es siempre el menor de los dos mínimos locales. Para escalar a más procesadores
sería necesario generalizar la partición a $p$ segmentos y usar un árbol de
reducciones, lo que queda fuera del alcance del presente proyecto.
