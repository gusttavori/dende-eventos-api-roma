import org.gradle.kotlin.dsl.implementation

plugins {
    id("java")
}

group = "br.com.softhouse.dende"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Dendê Framework (versão atualizada)
    implementation("io.github.lasilva:dendeframework:1.0.2")

    // Jackson para JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    implementation("com.h2database:h2:2.3.232")
    // HikariCP para conexões
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Lombok (opcional, mas recomendado)
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.test {
    useJUnitPlatform()
}