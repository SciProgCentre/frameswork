package space.kscience.frameswork.features.processor.common

import dev.inmo.micro_utils.koin.singleWithRandomQualifier
import dev.inmo.micro_utils.startup.plugin.StartPlugin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.koin.core.Koin
import org.koin.core.module.Module
import space.kscience.frameswork.features.processor.common.services.FramesProcessorMiddleware
import space.kscience.frameswork.features.processor.common.services.middlewares.BufferedImageSaverMiddleware

object JVMPlugin : StartPlugin {
    @Serializable
    private data class BufferedImageSaverMiddlewareConfig(
        val saveFolder: String,
    )

    override fun Module.setupDI(config: JsonObject) {
        config["images_saver"] ?.let {
            single { _ ->
                get<Json>().decodeFromJsonElement(BufferedImageSaverMiddlewareConfig.serializer(), it)
            }
        }
        with (Plugin) { setupDI(config) }
        singleWithRandomQualifier<FramesProcessorMiddleware> {
            val config = getOrNull<BufferedImageSaverMiddlewareConfig>()
            BufferedImageSaverMiddleware(config ?.saveFolder ?: "./local/")
        }

        singleWithRandomQualifier<FramesProcessorMiddleware.Factory> {
            val config = getOrNull<BufferedImageSaverMiddlewareConfig>()
            BufferedImageSaverMiddleware.Factory(config ?.saveFolder ?: "./local/")
        }
    }

    override suspend fun startPlugin(koin: Koin) {
        super.startPlugin(koin)
        Plugin.startPlugin(koin)
    }
}