// Plugins para generar una app Android, compilar Kotlin y procesar las funciones Compose.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    // Paquete de las clases Android generadas, como R.
    namespace = "com.pokedex.app"
    // SDK disponible al compilar; minSdk define el Android mínimo que puede ejecutar la app.
    compileSdk = 36
    defaultConfig {
        // Identificador de instalación de la aplicación en el dispositivo.
        applicationId = "com.pokedex.app"
        minSdk = 24
        // Declara la versión de Android para cuyos comportamientos se prepara la app.
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    // Habilita la generación de interfaz con Jetpack Compose.
    buildFeatures { compose = true }
    // Java y Kotlin generan bytecode con el mismo destino JVM 17.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies {
    // El BOM coordina las versiones de Compose; no agrega por sí mismo los componentes.
    implementation(platform("androidx.compose:compose-bom:2026.06.01"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    // Integración del ViewModel, su ámbito de corrutinas y observación del estado en Compose.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    // Cliente HTTP declarativo y convertidor de respuestas JSON a objetos Kotlin.
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // Componente de imágenes y transporte de red, requerido por Coil 3 para URLs remotas.
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    // Solo para pruebas: aserciones, control de corrutinas y servidor HTTP local.
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
