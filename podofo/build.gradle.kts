import com.android.ndkports.CMakeCompatibleVersion
import com.android.ndkports.CMakePortTask
import com.android.ndkports.PrefabSysrootPlugin

val portVersion = "0.9.7"

group = "com.android.ndk.thirdparty"
version = "$portVersion${rootProject.extra.get("snapshotSuffix")}"

plugins {
    id("maven-publish")
    id("com.android.ndkports.NdkPorts")
}

dependencies {
    implementation(project(":freetype"))
}

ndkPorts {
    ndkPath.set(File(project.findProperty("ndkPath") as String))
    source.set(project.file("podofo-$portVersion.tar.gz"))
    minSdkVersion.set(16)
}

tasks.prefab {
    generator.set(PrefabSysrootPlugin::class.java)
}

val buildTask = tasks.register<CMakePortTask>("buildPort") {
    cmake {
        arg("-DPODOFO_BUILD_SHARED=true")
        arg("-DPODOFO_BUILD_STATIC=false")
        arg("-DPODOFO_BUILD_LIB_ONLY=true")
        arg("-DPODOFO_NO_FONTMANAGER=true")
        arg("-DCMAKE_FIND_ROOT_PATH=$sysroot")
        arg("-DFREETYPE_INCLUDE_DIR=$sysroot/include/freetype2/")
        arg("-DCMAKE_DISABLE_FIND_PACKAGE_LIBCRYPTO=true")
    }
}

tasks.prefabPackage {
    version.set(CMakeCompatibleVersion.parse(portVersion))
    licensePath.set("COPYING")

    @Suppress("UnstableApiUsage") dependencies.set(
        mapOf(
            "freetype" to "2.11.0"
        )
    )

    modules {
        create("podofo") {
            dependencies.set(
                listOf(
                    "//freetype:freetype"
                )
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["prefab"])
            pom {
                name.set("podofo")
                description.set("The ndkports AAR for podofo.")
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jjanku/ndkports")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            url = uri("${rootProject.buildDir}/repository")
        }
    }
}
