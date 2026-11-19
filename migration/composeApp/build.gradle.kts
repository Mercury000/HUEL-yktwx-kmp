plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

val originalComponents = rootProject.projectDir.resolve("../app/src/main/java/com/mercury/yktwx/ui/components")
val originalPaymentPage = originalComponents.parentFile.resolve("pages/PaymentPage.kt")
val prepareUiSources = tasks.register("prepareUiSources") {
    inputs.files(listOf("BarBlur.kt", "AppIcons.kt", "PaymentCodeNumber.kt", "AppAppearance.kt", "AppearanceControls.kt").map(originalComponents::resolve))
    inputs.file(originalPaymentPage)
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
        val imports = appearance.substringBefore("internal val LocalGlassStyle")
        val hosts = appearance.substringAfter("internal val LocalGlassStyle")
            .substringBefore("@Composable\ninternal fun AppOverlayBottomSheet")
        val bars = "@Composable\ninternal fun AppTopAppBar" + appearance
            .substringAfter("@Composable\ninternal fun AppTopAppBar")
            .substringBefore("@Composable\nprivate fun InlineGlassTabs")
        // beta01 keeps BlurTopAppBar internal. The probe's navigation alpha is
        // always 1, so its zero-blur fallback can use the public TopAppBar.
        // The original GlassTopAppBar branch stays unchanged.
        val portableBars = bars
            .replace("BlurTopAppBar(title = title, largeTitleBlurRadius = 0.dp,", "TopAppBar(title = title,")
            .replace("titleAlpha = navigationTitleAlpha, bottomContent = bottomContent)", "bottomContent = bottomContent)")
        folder.resolve("OriginalAppearance.kt").writeText(imports + "internal val LocalGlassStyle" + hosts + portableBars)
        val controls = originalComponents.resolve("AppearanceControls.kt").readText()
            .substringBefore("/** Preserve page cards")
        folder.resolve("OriginalControls.kt").writeText(controls)
        val paymentSource = originalPaymentPage.readText().replace("\r\n", "\n")
        val measurements = paymentSource.substringAfter("/** Reserve the padded status pill")
            .substringBefore("/** Frozen cards")
            .replace("private fun payment", "internal fun payment")
        folder.resolve("OriginalPaymentMeasurements.kt").writeText("""
            package com.mercury.yktwx.migration
            import androidx.compose.runtime.*
            import androidx.compose.ui.platform.LocalDensity
            import androidx.compose.ui.text.AnnotatedString
            import androidx.compose.ui.text.TextStyle
            import androidx.compose.ui.text.rememberTextMeasurer
            import androidx.compose.ui.text.font.FontWeight
            import androidx.compose.ui.unit.*
            /** Reserve the padded status pill$measurements
        """.trimIndent())
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
        named("desktopMain") { dependencies { implementation(compose.desktop.currentOs) } }
    }
}
compose.desktop { application { mainClass = "com.mercury.yktwx.migration.DesktopMainKt" } }
compose.resources { packageOfResClass = "com.mercury.yktwx.migration.resources" }
