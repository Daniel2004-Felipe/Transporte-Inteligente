<<<<<<< HEAD



=======
>>>>>>> c3628db39dc62b472976c8f5187b5eb85aa3341d
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.transporteinteligenteapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.transporteinteligenteapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

<<<<<<< HEAD

=======
>>>>>>> c3628db39dc62b472976c8f5187b5eb85aa3341d
dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
<<<<<<< HEAD
    implementation(libs.play.services.location)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation("org.osmdroid:osmdroid-wms:6.1.14")
}
=======
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
>>>>>>> c3628db39dc62b472976c8f5187b5eb85aa3341d
