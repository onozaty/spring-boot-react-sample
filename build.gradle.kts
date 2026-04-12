plugins {
    id("com.diffplug.spotless") version "8.4.0"
}

repositories {
    mavenCentral()
}

spotless {
    kotlinGradle {
        ktlint()
    }
}
