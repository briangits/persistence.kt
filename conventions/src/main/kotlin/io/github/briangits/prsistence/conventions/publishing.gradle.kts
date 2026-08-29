package io.github.briangits.prsistence.conventions

import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("com.vanniktech.maven.publish")
}

interface Artifact {
    val name: Property<String>
    val description: Property<String>
}

val artifact = project.extensions.create<Artifact>("artifact")
artifact.name.convention("${rootProject.name}-${project.name}")
artifact.description.convention("")

afterEvaluate {
    val artifact = object {
        val name = artifact.name.get()
        val description = artifact.description.get()
    }

    project.extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(automaticRelease = true)
        signAllPublications()

        coordinates(group.toString(), artifact.name, version.toString())

        pom {
            name = artifact.name
            description = artifact.description
            inceptionYear = "2026"

            url = "https://github.com/briangits/persistence"

            licenses {
                license {
                    name = "The Apache License, Version 2.0"
                    url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            }

            developers {
                developer {
                    id = "briangits"
                    name = "Gits"
                    url = "https://github.com/briangits"
                }
            }

            scm {
                url = "https://github.com/briangits/persistence"
                connection = "scm:git:git://github.com/briangits/persistence.git"
                developerConnection = "scm:git:git://github.com/briangits/persistence.git"
            }
        }
    }
}
