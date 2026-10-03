package space.kscience.frameswork.features.ui.panel.frames

import dev.inmo.micro_utils.koin.singleWithRandomQualifier
import dev.inmo.micro_utils.startup.plugin.StartPlugin
import dev.inmo.navigation.core.NavigationNodeFactory
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.Koin
import org.koin.core.module.Module
import space.kscience.frameswork.features.common.web.models.ViewConfig
import space.kscience.frameswork.features.ui.panel.frames.ui.camera.CameraPanelViewConfigProvider
import space.kscience.frameswork.features.ui.panel.frames.ui.camera.CameraView
import space.kscience.frameswork.features.ui.panel.frames.ui.camera.CameraViewConfig
import space.kscience.frameswork.features.ui.panel.ui.PanelViewConfigProvider

object JSPlugin : StartPlugin {
    override fun Module.setupDI(config: JsonObject) {
        with(Plugin) { setupDI(config) }
    }

    override suspend fun startPlugin(koin: Koin) {
        super.startPlugin(koin)
        Plugin.startPlugin(koin)
    }
}

/**
 * Registers the camera MVVM navigation components in this module.
 *
 * Provides a typed navigation-node factory that creates [CameraView] instances for
 * [CameraViewConfig] configurations, along with the camera panel view-configuration provider.
 */
fun Module.includeBaseCameraJsMVVMParts() {
    singleWithRandomQualifier<NavigationNodeFactory<ViewConfig>> {
        NavigationNodeFactory.Typed<CameraViewConfig, ViewConfig> { chain, config ->
            CameraView(chain, config)
        }
    }

    singleWithRandomQualifier<PanelViewConfigProvider> { CameraPanelViewConfigProvider(get()) }
}
