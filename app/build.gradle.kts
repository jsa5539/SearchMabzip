import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Load Kakao REST API key from local.properties
// Load Kakao REST API key from local.properties
val localProps = Properties().apply { // ✅ 'java.util.'을 제거하고 Properties()만 사용합니다.
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val kakaoRestKey: String = (localProps.getProperty("KAKAO_REST_API_KEY") ?: "").trim()

android {
    namespace = "app.dku.searchmabzip"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.dku.searchmabzip"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expose Kakao REST key as a string resource
        resValue("string", "kakao_rest_api_key", kakaoRestKey)
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // HTTP 통신 및 JSON 파싱 라이브러리 추가
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // (선택 사항) 이미지 로딩 라이브러리 추가 (Glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}
