plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

import java.util.Properties
import java.io.FileInputStream


val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    val inputStream = FileInputStream(localPropertiesFile)
    localProperties.load(inputStream)
    inputStream.close()
}
val supabaseUrlProp = localProperties.getProperty("supabase.url") ?: "https://your-project.supabase.co"
val supabaseAnonKeyProp = localProperties.getProperty("supabase.anon.key") ?: "your-anon-public-key"


android {
    namespace = "com.example.vietsyncmobile"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.vietsyncmobile"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrlProp\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKeyProp\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res",
                "src/main/res/layout/group1_core_auth",
                "src/main/res/layout/group2_fragments_flows",
                "src/main/res/layout/group3_dialogs_layouts_items",
                "src/main/res/layout/group4_general_search_notifications"
            )
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)


    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.security.crypto)


    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)


    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.gotrue)


    implementation(libs.glide)
    implementation(libs.circleimageview)
    implementation(libs.lottie)
    implementation(libs.androidx.datastore.preferences.rxjava3)
    implementation(libs.rxandroid)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.swiperefreshlayout)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.work.runtime)
    implementation(libs.zxing.core)
    implementation(libs.mpandroidchart)
    implementation(libs.firebase.messaging)


    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.contrib)
    androidTestImplementation(libs.androidx.test.espresso.intents)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}