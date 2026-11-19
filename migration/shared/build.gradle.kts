plugins { kotlin("multiplatform") }

val sourceRoot = rootProject.projectDir.resolve("../app/src")
val prepareSharedSources by tasks.registering(Sync::class) {
    from(sourceRoot.resolve("main/java")) {
        include("**/data/Models.kt", "**/data/ReadFailure.kt", "**/data/NetworkReadAccess.kt", "**/data/ReadController.kt")
        // Keep the original model and all business rules; replace its single JVM clock call.
        filter { line -> line.replace("System.currentTimeMillis()", "kotlin.time.Clock.System.now().toEpochMilliseconds()") }
    }
    into(layout.buildDirectory.dir("generated/sharedMain"))
}
val prepareSharedTests by tasks.registering(Sync::class) {
    from(sourceRoot.resolve("test/java")) {
        include("**/data/KeyboardEncoderTest.kt", "**/data/ReadGateTest.kt", "**/data/ReadControllerTest.kt")
        filter { line ->
            line.replace("import org.junit.Assert.*", "import kotlin.test.*")
                .replace("import org.junit.Test", "import kotlin.test.Test")
                .replace("assertThrows(YktException::class.java)", "assertFailsWith<YktException>")
        }
    }
    into(layout.buildDirectory.dir("generated/sharedTest"))
}
kotlin {
    jvm("desktop")
    iosArm64()
    iosSimulatorArm64()
    jvmToolchain(21)
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain { kotlin.srcDir(prepareSharedSources) }
        commonTest { kotlin.srcDir(prepareSharedTests) }
        commonMain.dependencies { implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0") }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
        }
    }
}
