plugins {
    id("com.android.application")
}

// OneDrive y las rutas largas hacen fallar a aapt2: lo compilado va a %LOCALAPPDATA%\EurekaBankGR8_SOAP
// (equivale al Directory.Build.props del cliente MAUI de TG.1.4)
val carpetaLocal = System.getenv("LOCALAPPDATA")
if (carpetaLocal != null) {
    layout.buildDirectory.set(file("$carpetaLocal/EurekaBankGR8_SOAP/app"))
}

android {
    namespace = "ec.edu.monster"
    compileSdk = 36

    defaultConfig {
        applicationId = "ec.edu.monster.eurekabank.soap.gr08"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Las pruebas (JUnit 5, paquete ec.edu.monster.pruebas) se ejecutan en la PC, sin emulador
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    // AndroidX trae kotlin-stdlib 1.8 y kotlin-stdlib-jdk7/jdk8 1.6 (clases duplicadas): se alinean a una versión
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    // Cliente SOAP para Android: arma y lee los sobres XML del servicio WSEurekaBank
    implementation("com.google.code.ksoap2-android:ksoap2-android:3.6.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}
