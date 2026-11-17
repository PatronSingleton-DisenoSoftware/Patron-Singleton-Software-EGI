# 6. Pruebas

## 6.1. Estrategia

El proyecto no usa todavía un framework de pruebas (JUnit). La verificación se hace con
un `main` de demostración para el Singleton y, cuando el dominio esté completo, con casos
funcionales sobre `ControlAcceso`.

## 6.2. Casos de prueba

### Patrón Singleton

| ID | Caso | Acción | Resultado esperado | Estado |
|----|------|--------|--------------------|--------|
| CP-01 | Unicidad de la instancia | Comparar dos llamadas a `getInstancia()` con `==` | `true` | Pendiente (Main vacío) |
| CP-02 | Constructor inaccesible | Intentar `new ControlAcceso()` | Error de compilación | ✅ Por inspección |
| CP-03 | Instancia no nula | `getInstancia() != null` | `true` | Pendiente |
| CP-04 | Mismo objeto en llamadas repetidas | 100 llamadas en un bucle, comparar todas | Todas iguales a la primera | Pendiente |

### Reglas de negocio

| ID | Regla | Acción | Resultado esperado | Estado |
|----|-------|--------|--------------------|--------|
| CP-05 | RN-01 | Empleado `INTERNO` (2) → sector `INTERNO` (2) | Autorizado | Pendiente |
| CP-06 | RN-01 | Empleado `INTERNO` (2) → sector `TECNICO` (4) | Rechazado, motivo "Nivel insuficiente" | Pendiente |
| CP-07 | RN-01 | Empleado `RESTRINGIDO` (3) → sector `GENERAL` (1) | Autorizado (niveles acumulativos) | Pendiente |
| CP-08 | RN-02 | Empleado `INTERNO` (2) con permiso `ESPECIAL` activo → sector `TECNICO` (4) | Autorizado | Pendiente |
| CP-09 | RN-03 | Mismo caso con el permiso revocado | Rechazado | Pendiente |
| CP-10 | RN-04 | Acceso permitido | `RegistroAcceso` con `autorizado = true` y `fechaHora` no nula | Pendiente |
| CP-11 | RN-05 | Acceso denegado | `autorizado = false` y `motivo` no vacío | Pendiente |
| CP-12 | RN-07 | Conceder permiso a un empleado o sector no registrado | Operación rechazada, sin permiso creado | Pendiente |
| CP-13 | RN-03 | Revocar un permiso | El permiso sigue en la colección con `activo = false` | Pendiente |

### Compilación

| ID | Caso | Acción | Resultado esperado | Estado |
|----|------|--------|--------------------|--------|
| CP-14 | Compilación limpia | `javac` sobre todo `src/` | Sin errores | ✅ Verificado |

## 6.3. Código propuesto para `Main` (CP-01 a CP-03)

> Estado: **propuesto**. `Main` continúa vacío en el repositorio.

```java
public class Main {
    public static void main(String[] args) {
        ControlAcceso primera = ControlAcceso.getInstancia();
        ControlAcceso segunda = ControlAcceso.getInstancia();

        System.out.println("primera  = " + primera);
        System.out.println("segunda  = " + segunda);
        System.out.println("misma instancia? " + (primera == segunda));
    }
}
```

Salida esperada (la dirección puede variar):

```
primera  = ControlAcceso@1b6d3586
segunda  = ControlAcceso@1b6d3586
misma instancia? true
```

El número hexadecimal es el `hashCode` por defecto: al ser idéntico en ambas líneas, queda
demostrado que se trata del mismo objeto.

## 6.4. Cómo ejecutar

### Desde IntelliJ IDEA

1. Abrir `src/Main.java`.
2. Clic en el triángulo verde del margen, junto a `main` → **Run 'Main.main()'**.

### Desde la terminal

```powershell
javac -d out (Get-ChildItem src\*.java).FullName
java -cp out Main
```

## 6.5. Pruebas pendientes de automatizar

Cuando el dominio esté implementado se recomienda incorporar JUnit 5 y migrar CP-01 a
CP-13 a pruebas automáticas:

```java
@Test
void getInstanciaDevuelveSiempreLaMismaReferencia() {
    assertSame(ControlAcceso.getInstancia(), ControlAcceso.getInstancia());
}

@Test
void permisoRevocadoDeniegaElAcceso() {
    // ... alta de empleado, sector y permiso, luego revocar
    assertFalse(control.verificarAcceso(empleado, sector));
}

@Test
void nivelSuperiorAlcanzaSectoresDeNivelMenor() {
    empleado.setNivelAcceso(NivelAcceso.RESTRINGIDO);
    sector.setNivelRequerido(NivelAcceso.GENERAL);
    assertTrue(control.verificarAcceso(empleado, sector));
}
```

---

[Volver al índice](../README.md) · [← Anterior: Implementación](05-implementacion.md) · [Siguiente: Conclusiones →](07-conclusiones.md)
