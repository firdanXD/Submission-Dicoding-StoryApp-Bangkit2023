plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.firdan.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.firdan.storyapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true

    }
}



    dependencies {
        // Core
        implementation ("androidx.core:core-ktx:1.12.0")
        implementation ("androidx.appcompat:appcompat:1.6.1")
        implementation ("com.google.android.material:material:1.10.0")
        implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation ("androidx.legacy:legacy-support-v4:1.0.0")
        implementation ("com.google.guava:guava:29.0-android")

        testImplementation ("junit:junit:4.13.2")
        testImplementation ("pl.pragmatists:JUnitParams:1.1.1")
        androidTestImplementation ("androidx.test.ext:junit:1.1.5")
        testImplementation ("androidx.arch.core:core-testing:2.2.0")
        androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")
        debugImplementation ("androidx.fragment:fragment-testing:1.6.2")
        implementation("com.google.dagger:hilt-android:2.44")
        kapt("com.google.dagger:hilt-android-compiler:2.44")


        implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
        implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")


        implementation ("androidx.activity:activity-ktx:1.8.1")

        implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation ("androidx.viewpager2:viewpager2:1.0.0")


        implementation ("com.github.bumptech.glide:glide:4.16.0")


        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")


        implementation ("androidx.room:room-ktx:2.4.2")
        implementation ("androidx.room:room-paging:2.4.2")
        kapt ("androidx.room:room-compiler:2.4.2")

        implementation ("androidx.camera:camera-camera2:1.1.0-beta03")
        implementation ("androidx.camera:camera-lifecycle:1.1.0-beta03")
        implementation ("androidx.camera:camera-view:1.1.0-beta03")


        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
        testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
        testImplementation ("app.cash.turbine:turbine:0.7.0")


        implementation ("androidx.datastore:datastore-preferences:1.0.0")

        implementation ("com.google.android.gms:play-services-maps:18.0.2")
        implementation ("com.google.maps.android:android-maps-utils:2.3.0")
        implementation ("com.google.maps.android:maps-utils-ktx:3.4.0")
        implementation ("com.google.android.gms:play-services-location:19.0.1")


        implementation ("androidx.paging:paging-runtime-ktx:3.1.1")


        testImplementation ("org.mockito:mockito-core:3.12.4")
        testImplementation ("org.mockito:mockito-inline:3.12.4")

        androidTestImplementation ("com.squareup.okhttp3:mockwebserver:4.9.3")
        androidTestImplementation ("com.squareup.okhttp3:okhttp-tls:4.9.3")


        androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
        implementation ("androidx.test.espresso:espresso-contrib:3.4.0")
        androidTestImplementation ("com.android.support.test.espresso:espresso-contrib:3.0.2")
        implementation ("androidx.test.espresso:espresso-idling-resource:3.4.0")

        implementation ("androidx.multidex:multidex:2.0.1")



    }
kapt {
    correctErrorTypes = true
}

