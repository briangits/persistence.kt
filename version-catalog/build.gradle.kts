plugins {
    `version-catalog`
}


catalog {
    val group = rootProject.group.toString()
    val version = rootProject.version.toString()

    versionCatalog {
        version("persistence", version)

        library("core", group, "core").versionRef("persistence")
        library("exposed", group, "exposed").versionRef("persistence")
    }
}
