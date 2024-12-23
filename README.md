# CRUD with Kotlin 2.0.21 and Javalin 6.4.0 in OpenJDK 23

This is a simple CRUD API with Kotlin and Javalin from (https://javalin.io/tutorials/simple-kotlin-example).

To run with openjdk-23.0.1 and kotlin 2.0.21, because that version of kotlin is not compatible with the version of Java, the build.gradle.kts needs to be modified as follows:

```gradle
dependencies {
    implementation("io.javalin:javalin:6.4.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

   ...
}

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}
```
