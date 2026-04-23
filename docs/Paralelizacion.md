# Informe de paralelización

**Integrantes:** [completar]

---

## Estrategia de paralelización

Describa aquí qué estrategia utilizó para paralelizar cada función y por qué.

Ejemplo de estructura esperada:

- **`choquesPar`**: se divide el vector de cursos en dos mitades; cada mitad calcula
  los choques parciales en paralelo y se suman los resultados.
- **`generarAsignacionesPar`**: se usa `parallel` sobre los valores del primer
  índice para construir sub-vectores de asignaciones en paralelo.
- **`asignacionOptimaPar`**: se divide el espacio de candidatos en dos mitades;
  cada mitad busca su mínimo local en paralelo y se compara al final.

---

## Resultados experimentales

Complete la tabla con los tiempos medidos en su máquina.

| Cursos $n$ | Aulas $m$ | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
|:----------:|:---------:|:--------------:|:-------------:|:---------------:|
| 4          | 3         |                |               |                 |
| 6          | 4         |                |               |                 |
| 7          | 5         |                |               |                 |
| 8          | 5         |                |               |                 |

> Use `org.scalameter` para medir los tiempos:
> ```scala
> import org.scalameter._
> val t = measure { asignacionOptima(cursos, aulas, d, w) }
> println(s"Secuencial: $t ms")
> ```

---

## Análisis con la ley de Amdahl

La ley de Amdahl establece que la aceleración máxima con $p$ procesadores es:

$$S(p) = \frac{1}{(1 - \alpha) + \frac{\alpha}{p}}$$

donde $\alpha$ es la fracción del programa que se puede paralelizar.

Explique aquí:

1. Qué fracción del cómputo logró paralelizar en cada función.
2. En qué pares $(n, m)$ el paralelismo genera ganancias significativas y por qué.
3. En qué casos pequeños el paralelismo introduce sobrecarga (speedup negativo).

---

## Conclusiones de paralelización

[Completar con sus propias palabras]
