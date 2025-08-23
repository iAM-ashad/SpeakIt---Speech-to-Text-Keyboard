    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.kotlin.compose)
        alias(libs.plugins.hilt.android)
        alias(libs.plugins.kotlin.kapt)
    }

    android {
        namespace = "com.iamashad.speakit"
        compileSdk = 36

        buildFeatures {
            compose = true
            buildConfig = true
        }

        defaultConfig {
            applicationId = "com.iamashad.speakit"
            minSdk = 24
            targetSdk = 36
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            buildConfigField("String", "GROQ_BASE_URL", "\"https://api.groq.com/openai/\"")
            buildConfigField(
                "String",
                "GROQ_API_KEY",
                "\"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\""
            )
            buildConfigField("String", "WHISPER_MODEL", "\"whisper-large-v3\"")
        }

        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }
        kotlin {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                freeCompilerArgs.addAll(
                    "-Xcontext-receivers" // if you need context receivers
                )
            }
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    kapt {
        correctErrorTypes = true
    }

    dependencies {
        // Core + Compose (yours unchanged)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)

        // Lifecycle (owners live in lifecycle-runtime)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.compose)

        implementation(libs.androidx.savedstate.ktx)
        implementation(libs.androidx.lifecycle.runtime.v292)
        implementation(libs.androidx.lifecycle.runtime.ktx.v292)
        implementation(libs.androidx.lifecycle.common.java8)



        // Hilt
        implementation(libs.hilt.android)
        kapt(libs.hilt.compiler)

        // Coroutines
        implementation(libs.kotlinx.coroutines.android)

        // Networking
        implementation(libs.retrofit)
        implementation(libs.retrofit.converter.moshi)
        implementation(libs.okhttp.logging)
        implementation(libs.moshi.kotlin)
        coreLibraryDesugaring(libs.desugar.jdk)
        implementation("com.squareup.retrofit2:converter-scalars:2.11.0")

        // Lottie
        implementation("com.airbnb.android:lottie-compose:6.6.7")
        implementation("androidx.compose.material3:material3:1.3.2")

        // Constraint Layouts
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")


        // Tests…
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
