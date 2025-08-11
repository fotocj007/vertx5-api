
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
  java
  application
}

group = "com.game"
version = "1.0.0-SNAPSHOT"
val dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

repositories {
  mavenCentral()
}

val vertxVersion = "5.0.2"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "com.game.MainVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-launcher-application")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-openapi")
  implementation("io.vertx:vertx-web-openapi-router")
  implementation("io.vertx:vertx-redis-client")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")

  // Log4j2 依赖
  implementation("org.apache.logging.log4j:log4j-core:2.20.0")
  implementation("org.apache.logging.log4j:log4j-api:2.20.0")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

// 明确指定使用Java 21工具链
tasks.withType<JavaCompile> {
  options.compilerArgs.add("--enable-preview")
  options.encoding = "UTF-8"
}

tasks.withType<JavaExec> {
  jvmArgs("--enable-preview")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  // 仅为 run 任务设置 args
  if (name == "run") {
    args = listOf("run", mainVerticleName)
  }
}

// 2. 配置 distZip 任务
tasks.named<Zip>("distZip") {
  // 将版本号置空，这样就不会出现 "-1.0.0-SNAPSHOT"
  archiveVersion.set("")
  // 将日期字符串作为文件名的附加部分
  archiveAppendix.set(dateString)
}

// 3. 配置 distTar 任务，与 distZip 保持一致
tasks.named<Tar>("distTar") {
  archiveVersion.set("")
  archiveAppendix.set(dateString)
}
