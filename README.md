# App1_Firebase – Control y monitoreo de circuito IoT

Aplicación Android (Kotlin) para **controlar y monitorear un circuito IoT** (ESP32 con sensor
ultrasónico de distancia, simulado) desde **dos o más celulares a la vez**, usando
**Firebase Realtime Database** por WiFi / datos móviles.

## 1. Problemática y funcionalidades

En faenas e industrias (entorno OT) se necesita activar y desactivar equipos a distancia y saber
quién lo hizo, cuándo, y qué detectaron los sensores (por ejemplo, un obstáculo cerca de una máquina).

| Pantalla | Funcionalidad |
|---|---|
| Login | Inicio de sesión con RUT y contraseña validados contra Firebase. |
| Crear Cuenta | Registro de usuario (RUT, nombre, apellido, correo, cargo, contraseña). |
| Menú | Muestra el nombre del usuario y redirecciona a cada función. |
| Manipular Circuito | Activar / Desactivar el circuito y simular la lectura del sensor. El estado se sincroniza en tiempo real en todos los celulares e indica si hay conexión. |
| Ver Acciones | Listado de fecha, hora, usuario, acción y distancia. |
| Estadísticas | Cantidad de activaciones, desactivaciones, obstáculos detectados y distancia total. |
| Ver Perfil | Datos del usuario que inició sesión (contraseña oculta). |

## 2. Herramientas de desarrollo (justificación)

| Herramienta | Motivo |
|---|---|
| **Android Studio** | IDE oficial de Google para Android: emuladores, diseño de pantallas y depuración. |
| **Kotlin** | Lenguaje oficial recomendado por Google para Android. |
| **Gradle (Kotlin DSL) + catálogo de versiones** | Sistema de compilación estándar; las versiones de librerías se centralizan en `gradle/libs.versions.toml`. |
| **AndroidX + Material Design** | Componentes estándar y compatibles con distintas versiones de Android. |
| **Firebase Realtime Database** | Base de datos en la nube que sincroniza datos entre dispositivos en tiempo real. |
| **Firebase Authentication (anónima)** | Permite que las reglas de seguridad exijan una sesión válida. |
| **GitHub** | Control de versiones del código. |

Estructura: una Activity por pantalla, con la lógica en el archivo `.kt` y el diseño en el `.xml`.
Versión mínima: Android 13 (API 33).

## 3. Comunicación inalámbrica

Se eligió **WiFi / Internet con Firebase** en lugar de Bluetooth:

- Bluetooth tiene un alcance aproximado de 10 metros y conecta pocos dispositivos a la vez.
- Con WiFi, cualquier cantidad de celulares ve el mismo estado al instante, aunque estén lejos
  (útil en una planta o faena).
- El ESP32 tiene WiFi integrado, por lo que el circuito real puede leer y escribir el mismo nodo
  `circuito` en Firebase.
- Si un celular pierde la conexión, las acciones se guardan en el equipo y se envían al reconectar.

Permisos usados: `INTERNET` y `ACCESS_NETWORK_STATE`.

### Estructura de la base de datos

```
usuarios/{rut}   rut, nombre, apellido, correo, cargo, contrasena (hash)
acciones/{id}    fecha, hora, usuario, accion, distancia, timestamp
circuito         estado, usuario, fecha, hora, ultimaAccion, ultimaDistancia
```

## 4. Seguridad (ISO/IEC 27400 – IoT)

- **Credenciales:** la contraseña se guarda como hash SHA-256, nunca en texto plano.
- **Control de acceso:** bloqueo de 30 segundos tras 3 intentos fallidos.
- **Validaciones:** RUT con dígito verificador, correo válido y contraseña de mínimo 6 caracteres con letras y números.
- **Reglas de Firebase** (`database.rules.json`): solo se lee y escribe con sesión; una cuenta no se puede sobrescribir; el historial no se puede modificar ni borrar.
- **Comunicación cifrada:** `usesCleartextTraffic="false"` (solo HTTPS).
- **Datos locales:** `allowBackup="false"` y pantallas internas con `exported="false"`.

## 5. Cómo ejecutar

1. Abrir el proyecto en Android Studio y sincronizar Gradle.
2. Ejecutar en 2 emuladores o celulares con Internet.

## Autora

Danisa Gajardo – Sección D-IEI-N4-P2-C3, INACAP.
