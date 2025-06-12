import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.mavenPublish)
}

kotlin {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  androidTarget {
    publishLibraryVariants("release")
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
  ).forEach {
    it.binaries.framework {
      baseName = "com_sd_lib_kmp_compose_nested"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(compose.foundation)
    }
  }
}

android {
  namespace = "com.sd.lib.kmp.compose_nested"
  compileSdk = 35
  defaultConfig {
    minSdk = 21
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
