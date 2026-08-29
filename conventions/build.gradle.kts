plugins {
    `kotlin-dsl`
}

fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) {
    val artifact =
        plugin.get().let {
            "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
        }

    implementation(artifact)
}

dependencies {
    plugin(libutils.plugins.mavenPublish)
}
