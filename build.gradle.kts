plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
//    java
    `maven-publish`
}

//java {
//    sourceCompatibility = JavaVersion.VERSION_17
//    targetCompatibility = JavaVersion.VERSION_17
//}

//subprojects {
//    if(this.name == "CellularAutomatonCompose") {
//        afterEvaluate {
//            publishing {
//                publications {
//                    create<MavenPublication>("release") {
//                        groupId = this.groupId
//                        artifactId = this.name
//                        version = this.version
//                        from(components["kotlin"])
//                    }
//                }
//            }
//        }
//    }
//}