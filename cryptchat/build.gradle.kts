plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // Atualização para uma versão mais recente
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.0") // Certifique-se de ter uma versão compatível
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
