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

tasks.register("test") {
    group = "verification"
    description = "Run unit tests for backend and frontend (excluding E2E)"
    dependsOn(":backend:test", ":frontend:test")
}

tasks.register("lint") {
    group = "verification"
    description = "Run lint/static analysis for backend and frontend"
    dependsOn(
        ":backend:spotbugsMain",
        ":backend:spotbugsTest",
        ":frontend:lint",
        ":frontend:typecheck",
    )
}

tasks.register("format") {
    group = "formatting"
    description = "Format code for backend and frontend"
    dependsOn(":backend:spotlessApply", ":frontend:format")
}

tasks.register("e2eTest") {
    group = "verification"
    description = "Run E2E tests"
    dependsOn(":backend:e2eTest")
}

tasks.register("e2eTestHeaded") {
    group = "verification"
    description = "Run E2E tests with headed browser"
    dependsOn(":backend:e2eTestHeaded")
}
