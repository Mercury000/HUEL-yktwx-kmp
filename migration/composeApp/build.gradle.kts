plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

val originalComponents = rootProject.projectDir.resolve("../app/src/main/java/com/mercury/yktwx/ui/components")
val prepareUiSources by tasks.registering {
    inputs.files(listOf("BarBlur.kt", "AppIcons.kt", "PaymentCodeNumber.kt", "AppAppearance.kt", "AppearanceControls.kt").map(originalComponents::resolve))
    val destination = layout.buildDirectory.dir("generated/originalUi")
    outputs.dir(destination)
    doLast {
        val folder = destination.get().asFile
        folder.mkdirs()
        listOf("BarBlur.kt", "AppIcons.kt", "PaymentCodeNumber.kt").forEach { name ->
            originalComponents.resolve(name).copyTo(folder.resolve(name), overwrite = true)
        }
        // Reuse the original glass host/dialog implementation; leave Android shell/navigation outside.
        val appearance = originalComponents.resolve("AppAppearance.kt").readText().replace("\r\n", "\n")
        val imports = appearance.substringBefore("internal val LocalGlassStyle").lines()
            .filterNot { it.startsWith("import com.") }.joinToString("\n")
        val hosts = appearance.substringAfter("internal val LocalGlassStyle")
            .substringBefore("@Composable\ninternal fun AppOverlayBottomSheet")
        folder.resolve("OriginalAppearance.kt").writeText(imports + "internal val LocalGlassStyle" + hosts)
        val controls = originalComponents.resolve("AppearanceControls.kt").readText()
            .substringBefore("/** Preserve page cards")
        folder.resolve("OriginalControls.kt").writeText(controls)
    }
}
kotlin {
    jvm("desktop")
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "MigrationApp"
            isStatic = true
        }
    }
    jvmToolchain(21)
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain { kotlin.srcDir(prepareUiSources) }
        commonMain.dependencies {
            implementation(project(":shared"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            listOf("ui", "icons", "glass-icons", "material", "blur", "glass", "nav").forEach {
                implementation("top.yukonga.miuix.kmp:miuix-$it:0.9.5-beta01")
            }
        }
        desktopMain.dependencies { implementation(compose.desktop.currentOs) }
    }
}
compose.desktop { application { mainClass = "com.mercury.yktwx.migration.DesktopMainKt" } }
compose.resources { packageOfResClass = "com.mercury.yktwx.migration.resources" }
