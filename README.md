# Pokédex con Retrofit, MVVM y Jetpack Compose

Ejemplo de DSY1105, EA3, actividad 3.1. Consulta un Pokémon por nombre o número en PokéAPI y muestra su imagen, tipos, altura y peso con Material 3.

La app incluye validación de entrada, indicador de carga, mensajes de error, reintento y caché en memoria por nombre e ID.

## Abrir y ejecutar en Android Studio

1. Clona este repositorio o descarga y descomprime su ZIP.
2. Abre la carpeta raíz, donde está `settings.gradle.kts`.
3. Usa Android Studio con soporte para Android Gradle Plugin 8.13.1. Configura el JDK de Gradle en 21, que es el utilizado para compilar este ejemplo.
4. Instala Android SDK Platform 36 desde SDK Manager y acepta la sincronización de Gradle.
5. Selecciona un emulador o dispositivo Android API 24 o superior, con conexión a internet.
6. Ejecuta la configuración `app`. Escribe `pikachu` y presiona **Buscar**.

Android Studio configura la ruta del SDK en `local.properties`. Ese archivo es específico de cada equipo y no se versiona. La primera sincronización necesita internet para descargar dependencias.

## Compilar desde terminal

Linux o macOS:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Probar en clases

- `pikachu`, `25` y ` PIKACHU ` deben mostrar la misma ficha.
- Una entrada vacía muestra una validación sin consultar la API.
- `pokemon-inexistente-xyz` permite observar el manejo de un HTTP 404.
- Sin internet, busca un Pokémon que todavía no esté en la caché. Reactiva la conexión y presiona **Reintentar**.
- Rota el dispositivo para observar que el ViewModel conserva el estado durante el cambio de configuración.

La caché de datos dura mientras exista el Repository asociado al ViewModel; no persiste al finalizar el proceso. La app consulta únicamente cuando presionas Buscar o Reintentar. PokéAPI ofrece consultas GET y no requiere una clave.

## Estructura

```text
app/src/main/java/com/pokedex/app/
    MainActivity.kt
    data/model/PokemonDto.kt
    data/remote/PokeApi.kt
    data/remote/PokeApiClient.kt
    repository/PokemonRepository.kt
    viewmodel/PokedexUiState.kt
    viewmodel/PokedexViewModel.kt
    ui/screen/PantallaPokedex.kt
```

La pantalla comunica eventos al ViewModel y observa su estado. El ViewModel consulta al Repository. El Repository reutiliza datos o llama a la interfaz Retrofit. Gson convierte JSON a DTO y Coil carga la imagen desde su URL.

Las pruebas de `app/src/test/` cubren normalización y caché, conversión JSON y ruta HTTP con MockWebServer, entrada vacía, carga y resultado, prevención de consultas simultáneas, error de red con reintento y respuesta 404.

## Versiones del ejemplo

| Componente | Versión |
| --- | --- |
| Android Gradle Plugin | 8.13.1 |
| Gradle Wrapper | 8.13 |
| Kotlin | 2.4.0 |
| Compose BOM | 2026.06.01 |
| Lifecycle | 2.10.0 |
| Retrofit y converter-gson | 3.0.0 |
| Coil Compose y módulo de red | 3.3.0 |
| compileSdk / targetSdk | 36 |
| minSdk | 24 |

Material 3 utiliza la versión administrada por el Compose BOM. Los archivos Gradle fijan las dependencias del proyecto.

## Guía y referencias

- [Guía HTML del curso](https://github.com/ProfeMike-sudo/2026_2_SB_DSY1105/blob/main/Guias%20Blackboard/12_Retrofit_PokeAPI_MVVM_Compose.html)
- [Documentación de PokéAPI](https://pokeapi.co/docs/v2)
- [Retrofit](https://lysine.dev/retrofit/)
- [Estado en Compose](https://developer.android.com/develop/ui/compose/state)
- [Coil para Compose](https://coil-kt.github.io/coil/compose/)

Pokémon y sus personajes pertenecen a sus respectivos titulares. Este proyecto es un ejemplo educativo independiente y utiliza datos e imágenes proporcionados por PokéAPI.
