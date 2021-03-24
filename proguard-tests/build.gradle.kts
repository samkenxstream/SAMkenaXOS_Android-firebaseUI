plugins {
  id("com.android.application")
}

// This is always set to 'true' on Travis CI
val inCiBuild = System.getenv("CI") == "true"

android {
    compileSdkVersion(Config.SdkVersions.compile)

    defaultConfig {
        minSdkVersion(Config.SdkVersions.min)
        targetSdkVersion(Config.SdkVersions.target)

        versionName = Config.version
        versionCode = 1

        resourcePrefix("fui_")
        vectorDrawables.useSupportLibrary = true

        multiDexEnabled = true
    }

    buildTypes {
        named("debug").configure {
            // This empty config is only here to make Android Studio happy.
            // This build type is later ignored in the variantFilter section
        }

        named("release").configure {
            // For the purposes of the sample, allow testing of a proguarded release build
            // using the debug key
            signingConfig = signingConfigs["debug"]

            postprocessing {
                isRemoveUnusedCode = true
                isRemoveUnusedResources = true
                isObfuscate = true
            }
        }
    }

    lintOptions {
        // Common lint options across all modules
        disable(
            "ObsoleteLintCustomCheck", // ButterKnife will fix this in v9.0
            "IconExpectedSize",
            "InvalidPackage", // Firestore uses GRPC which makes lint mad
            "NewerVersionAvailable", "GradleDependency", // For reproducible builds
            "SelectableText", "SyntheticAccessor" // We almost never care about this
        )

        isCheckAllWarnings = true
        isWarningsAsErrors = true
        isAbortOnError = true

        baselineFile = file("$rootDir/library/quality/lint-baseline.xml")
    }

    variantFilter {
        if (inCiBuild && name == "debug") {
            ignore = true
        }
    }
}

dependencies {
    implementation(project(":auth"))
    implementation(project(":firestore"))
    implementation(project(":database"))
    implementation(project(":storage"))

    implementation(Config.Libs.Androidx.lifecycleExtensions)
}

apply(plugin = "com.google.gms.google-services")
