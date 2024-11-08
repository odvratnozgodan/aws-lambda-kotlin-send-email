import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.21"
    `java-library`
    id("com.gradleup.shadow") version "8.3.5"
}

group = "solutions.deliverit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("aws.sdk.kotlin:ses:0.33.1-beta")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.5")
}

tasks.register("generateConstants") {
    val props = Properties()
    File("secret.properties").inputStream().use { props.load(it) }
    val senderEmail = props.getProperty("senderEmail")
    val recipientEmail = props.getProperty("recipientEmail")
    val outputDir = file("${rootProject.layout.buildDirectory.get()}/generated/src")
    outputs.dir(outputDir)
    doLast {
        outputDir.mkdirs()
        val file = file("$outputDir/EmailConfig.kt")
        file.writeText(
            """
            object EmailConfig {
                const val SENDER_EMAIL = "$senderEmail"
                const val RECIPIENT_EMAIL = "$recipientEmail"
            }
        """.trimIndent()
        )
        println("generated constants to $file")
    }
}


tasks.prepareKotlinBuildScriptModel{
    dependsOn("generateConstants")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName("compileKotlin").dependsOn("generateConstants")

sourceSets {
    main {
        kotlin.srcDir("${layout.buildDirectory.get()}/generated/src")
    }
}

kotlin {
    jvmToolchain(17)
}
