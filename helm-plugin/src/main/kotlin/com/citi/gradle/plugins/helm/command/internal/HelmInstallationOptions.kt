package com.citi.gradle.plugins.helm.command.internal

import com.citi.gradle.plugins.helm.command.ConfigurableHelmInstallationOptions
import com.citi.gradle.plugins.helm.command.ConfigurableHelmServerOperationOptions
import com.citi.gradle.plugins.helm.command.HelmExecSpec
import com.citi.gradle.plugins.helm.command.HelmInstallationOptions
import com.citi.gradle.plugins.helm.command.HelmOptions
import com.citi.gradle.plugins.helm.command.HelmServerOperationOptions
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.slf4j.LoggerFactory
import org.unbrokendome.gradle.pluginutils.property
import org.unbrokendome.gradle.pluginutils.withDefault


fun HelmInstallationOptions.withDefaults(
    defaults: HelmInstallationOptions, providers: ProviderFactory
): HelmInstallationOptions =
    object : HelmInstallationOptions,
        HelmServerOperationOptions by withDefaults(defaults as HelmServerOperationOptions, providers) {

        override val atomic: Provider<Boolean>
            get() = this@withDefaults.atomic.withDefault(defaults.atomic, providers)

        override val devel: Provider<Boolean>
            get() = this@withDefaults.devel.withDefault(defaults.devel, providers)

        override val verify: Provider<Boolean>
            get() = this@withDefaults.verify.withDefault(defaults.verify, providers)

        override val wait: Provider<Boolean>
            get() = this@withDefaults.wait.withDefault(defaults.wait, providers)

        override val waitForJobs: Provider<Boolean>
            get() = this@withDefaults.waitForJobs.withDefault(defaults.waitForJobs, providers)

        override val version: Provider<String>
            get() = this@withDefaults.version.withDefault(defaults.version, providers)

        override val createNamespace: Provider<Boolean>
            get() = this@withDefaults.createNamespace.withDefault(defaults.createNamespace, providers)

        override val skipCrds: Provider<Boolean>
            get() = this@withDefaults.skipCrds.withDefault(defaults.skipCrds, providers)
    }


fun ConfigurableHelmInstallationOptions.conventionsFrom(source: HelmInstallationOptions) {
    conventionsFrom(source as HelmServerOperationOptions)
    atomic.convention(source.atomic)
    devel.convention(source.devel)
    verify.convention(source.verify)
    wait.convention(source.wait)
    waitForJobs.convention(source.waitForJobs)
    version.convention(source.version)
    createNamespace.convention(source.createNamespace)
    skipCrds.convention(source.skipCrds)
}


fun ConfigurableHelmInstallationOptions.setFrom(source: HelmInstallationOptions) {
    setFrom(source as HelmServerOperationOptions)
    atomic.set(source.atomic)
    devel.set(source.devel)
    verify.set(source.verify)
    wait.set(source.wait)
    waitForJobs.set(source.waitForJobs)
    version.set(source.version)
    createNamespace.set(source.createNamespace)
    skipCrds.convention(source.skipCrds)
}


data class HelmInstallationOptionsHolder(
    private val serverOperationOptions: ConfigurableHelmServerOperationOptions,
    override val atomic: Property<Boolean>,
    override val devel: Property<Boolean>,
    override val verify: Property<Boolean>,
    override val wait: Property<Boolean>,
    override val waitForJobs: Property<Boolean>,
    override val version: Property<String>,
    override val createNamespace: Property<Boolean>,
    override val skipCrds: Property<Boolean>,
) : ConfigurableHelmInstallationOptions,
    ConfigurableHelmServerOperationOptions by serverOperationOptions {

    constructor(objects: ObjectFactory)
            : this(
        serverOperationOptions = HelmServerOperationOptionsHolder(objects),
        atomic = objects.property(),
        devel = objects.property(),
        verify = objects.property(),
        wait = objects.property(),
        waitForJobs = objects.property(),
        version = objects.property(),
        createNamespace = objects.property(),
        skipCrds = objects.property(),
    )
}


object HelmInstallationOptionsApplier : HelmOptionsApplier {

    private val logger = LoggerFactory.getLogger(javaClass)


    override fun apply(spec: HelmExecSpec, options: HelmOptions) {
        if (options is HelmInstallationOptions) {

            logger.debug("Applying options: {}", options)

            with(spec) {
                flag("--atomic", options.atomic)
                flag("--devel", options.devel)
                flag("--verify", options.verify)
                flag("--wait", options.wait)
                flag("--wait-for-jobs", options.waitForJobs)
                option("--version", options.version)
                flag("--create-namespace", options.createNamespace)
                flag("--skip-crds", options.skipCrds)
            }
        }
    }
}
