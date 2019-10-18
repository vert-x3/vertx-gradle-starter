import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "4.0.3"
}

repositories {
  mavenCentral()
}

val vertxVersion = "3.8.3"
val junitVersion = "5.3.2"

dependencies {
  implementation("io.vertx:vertx-core:$vertxVersion")

  testImplementation("io.vertx:vertx-junit5:$vertxVersion")
  testImplementation("io.vertx:vertx-web-client:$vertxVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
  mainClassName = "io.vertx.core.Launcher"
}

val mainVerticleName = "io.vertx.starter.MainVerticle"
val watchForChange = "src/**/*.java"
val doOnChange = "${projectDir}/gradlew classes"

tasks {
  test {
    useJUnitPlatform()
  }

  getByName<JavaExec>("run") {
    args = listOf("run", mainVerticleName, "--redeploy=${watchForChange}", "--launcher-class=${application.mainClassName}", "--on-redeploy=${doOnChange}")
  }

  withType<ShadowJar> {
    classifier = "fat"
    manifest {
      attributes["Main-Verticle"] = mainVerticleName
    }
    mergeServiceFiles {
      include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
  }
}
