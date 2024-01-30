plugins {
    kotlin("jvm") version "1.9.20-RC2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

version = 1.1

repositories {
    maven("https://nexus.cyanbukkit.cn/repository/maven-public")
    maven("https://repo.loohpjames.com/repository")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("org.black_ixx:playerpoints:3.2.6")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    // fastjson
    implementation("com.alibaba:fastjson:1.2.83")
}

bukkit {
    main = "cn.cyanbukkit.aiimage.SpigotDraw"
    name = "CyanSpigotDraw"
    version = project.version.toString()
    description = ""
    website = "https://cyanbukkit.cn"
    depend = listOf("PlayerPoints")
}

kotlin {
    jvmToolchain(8)
}



tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("CyanSpigotDraw-${version}.jar")
    }

}