import com.android.ndkports.AndroidExecutableTestTask
import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask

val portVersion = "2.11.0"

group = "com.android.ndk.thirdparty"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
}

ndkPorts {
    ndkPath.set(File(project.findProperty("ndkPath") as String))
    source.set(project.file("freetype-$portVersion.tar.gz"))
    minSdkVersion.set(16)
}

val buildTask = tasks.register<CMakePortTask>("buildPort") {
    cmake {
        arg("-DBUILD_SHARED_LIBS=true")
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("LICENSE.TXT")

    modules {
        create("freetype")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("freetype")
                description.set("The ndkports AAR for freetype.")
            }
        }
    }

    repositories {
        maven {
            url = uri("${rootProject.buildDir}/repository")
        }
    }
}
