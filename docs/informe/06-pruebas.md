# 6. Pruebas

## 6.1. Estrategia

El proyecto implementa la verificación funcional mediante la ejecución por consola y pruebas de integración de las reglas de negocio. En la entrega actual, la validación se sustenta en:
1. Inspección de compilación limpia de todo el proyecto.
2. Verificación de la unicidad y encapsulamiento del Singleton.
3. Evaluación de las reglas de negocio (RN-01 a RN-07) a través de `verificarAcceso()` e historial.

## 6.2. Casos de prueba

### Patrón Singleton

| ID | Caso | Acción | Resultado esperado | Estado |
|----|------|--------|--------------------|--------|
| CP-01 | Unicidad de la instancia | Comparar dos llamadas a `getInstance()` con `==` | `true` | ✅ Verificado |
| CP-02 | Constructor inaccesible | Intentar `new ControlAcceso()` | Error de compilación | ✅ Por diseño (constructor `private`) |
| CP-03 | Instancia no nula | `getInstance() != null` | `true` | ✅ Verificado |
| CP-04 | Mismo objeto en llamadas repetidas | Múltiples llamadas en el ciclo de vida | Siempre la misma referencia | ✅ Verificado |

### Reglas de negocio

| ID | Regla | Acción | Resultado esperado | Estado |
|----|-------|--------|--------------------|--------|
| CP-05 | RN-01 | Empleado `NIVEL_2` → sector `NIVEL_2` | Autorizado | ✅ Verificado |
| CP-06 | RN-01 | Empleado `NIVEL_2` → sector `NIVEL_4` | Rechazado, motivo "Nivel insuficiente" | ✅ Verificado |
| CP-07 | RN-01 | Empleado `NIVEL_3` → sector `NIVEL_1` | Autorizado (niveles acumulativos por `getNivel()`) | ✅ Verificado |
| CP-08 | RN-02 | Empleado `NIVEL_2` con permiso `especial = true` y `activo = true` → sector `NIVEL_4` | Autorizado por permiso especial | ✅ Verificado |
| CP-09 | RN-03 | Mismo caso tras revocar el permiso | Rechazado, motivo "Nivel insuficiente" | ✅ Verificado |
| CP-10 | RN-04 | Acceso permitido | `RegistroAcceso` con `autorizado = true` y `fechaHora` con `now()` | ✅ Verificado |
| CP-11 | RN-05 | Acceso denegado | `autorizado = false` y `motivo` descriptivo | ✅ Verificado |
| CP-12 | RN-07 | Conceder permiso a un empleado o sector no registrado | Operación rechazada con aviso preventivo | ✅ Verificado |
| CP-13 | RN-03 | Revocar un permiso | Permiso conservado en la colección con `activo = false` | ✅ Verificado |

### Compilación

| ID | Caso | Acción | Resultado esperado | Estado |
|----|------|--------|--------------------|--------|
| CP-14 | Compilación limpia | `javac` sobre todo `src/` | Sin errores ni advertencias | ✅ Verificado |

## 6.3. Verificación de la instancia Singleton (CP-01 a CP-03)

Para comprobar la unicidad del Singleton:

```java
ControlAcceso primera = ControlAcceso.getInstance();
ControlAcceso segunda = ControlAcceso.getInstance();

System.out.println("primera  = " + primera);
System.out.println("segunda  = " + segunda);
System.out.println("misma instancia? " + (primera == segunda));
```

Salida esperada:

```
primera  = ControlAcceso@1b6d3586
segunda  = ControlAcceso@1b6d3586
misma instancia? true
```

Al ser idénticas las referencias, queda demostrado el cumplimiento del patrón Singleton.

## 6.4. Cómo ejecutar

### Desde IntelliJ IDEA

1. Abrir `src/Main.java`.
2. Clic en el triángulo verde del margen, junto a `main` → **Run 'Main.main()'**.

### Desde la terminal

```powershell
javac -d out (Get-ChildItem src\*.java).FullName
java -cp out Main
```

## 6.5. Pruebas automatizadas a futuro (JUnit 5)

Como evolución recomendada fuera del alcance estricto de la EGI, se pueden plantear pruebas unitarias automáticas con JUnit 5:

```java
@Test
void getInstanceDevuelveSiempreLaMismaReferencia() {
    assertSame(ControlAcceso.getInstance(), ControlAcceso.getInstance());
}

@Test
void permisoRevocadoDeniegaElAcceso() {
    // ... alta de empleado, sector y permiso, luego revocar
    assertFalse(control.verificarAcceso(1, 10));
}

@Test
void nivelSuperiorAlcanzaSectoresDeNivelMenor() {
    empleado.setNivelAcesso(NivelAcceso.NIVEL_3);
    sector.setNivelRequerido(NivelAcceso.NIVEL_1);
    assertTrue(control.verificarAcceso(1, 10));
}
```

---

[Volver al índice](../README.md) · [← Anterior: Implementación](05-implementacion.md) · [Siguiente: Conclusiones →](07-conclusiones.md)
