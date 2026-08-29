package io.github.briangits.persistence.conventions.publishing

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("com.vanniktech.maven.publish")

            val artifact = project.extensions.create<ArtifactExtension>("artifact").apply {
                name.convention(project.name)
                description.convention("")
            }

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
                        with(it) {
                            name.set(artifact.name)
                            description.set(artifact.description)
                            inceptionYear.set("2026")
                            url.set("https://github.com/briangits/persistence")

                            licenses {
                                it.license {
                                    it.name.set("The Apache License, Version 2.0")
                                    it.url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                    it.distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }

                            developers {
                                it.developer {
                                    it.id.set("briangits")
                                    it.name.set("Gits")
                                    it.url.set("https://github.com/briangits")
                                }
                            }

                            scm {
                                it.url.set("https://github.com/briangits/persistence")
                                it.connection.set("scm:git:git://github.com/briangits/persistence.git")
                                it.developerConnection.set("scm:git:git://github.com/briangits/persistence.git")
                            }
                        }
                    }
                }
            }
        }
    }
}
