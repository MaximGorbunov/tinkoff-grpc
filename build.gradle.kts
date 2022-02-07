import com.google.protobuf.gradle.*

plugins {
    id("nebula.release") version "15.3.1"
    id("maven-publish")
    id("java-library")
    id("idea")
    id("com.google.protobuf") version "0.8.13"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    `java`
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(grpc.bundles.javax)
    api(grpc.bundles.core)
    compileOnly(grpc.bundles.kotlin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "cf.mgorbunov"
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.17.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpc.versions.grpc.get()}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${grpc.versions.grpcKotlin.get()}:jdk7@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
    generatedFilesBaseDir = "$projectDir/src/generated"
}
sourceSets {
    getByName("main").java.srcDirs("/src/generated/")
}

val sourcesJar by tasks.creating(Jar::class) {
    val sourceSets: SourceSetContainer by project

    from(sourceSets["main"].allJava)
    archiveClassifier.set("sources")
}

tasks.named("release") {
    finalizedBy("publish")
}

publishing {
    publications {
        create<MavenPublication>("${name}") {
            from(components["java"])
            artifactId = "${name}"
        }
    }
    repositories {
        mavenLocal()
    }
}