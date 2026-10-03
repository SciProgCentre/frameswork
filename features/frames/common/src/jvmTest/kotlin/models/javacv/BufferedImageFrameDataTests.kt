package space.kscience.frameswork.features.frames.common.models.javacv

import dev.inmo.micro_utils.meta.buildMetaContainer
import kotlinx.coroutines.test.runTest
import space.kscience.frameswork.features.frames.common.models.SourceFrameSourceHeight
import space.kscience.frameswork.features.frames.common.models.SourceFrameSourceWidth
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** Tests JPEG conversion and metadata copying for [BufferedImageFrameData]. */
class BufferedImageFrameDataTests {
    /** Verifies that conversion produces a readable JPEG with the source dimensions. */
    @Test
    fun toByteArrayProducesReadableJpegWithOriginalDimensions() = runTest {
        val image = BufferedImage(4, 3, BufferedImage.TYPE_INT_RGB).apply {
            val graphics = createGraphics()
            try {
                graphics.color = Color(20, 80, 160)
                graphics.fillRect(0, 0, width, height)
            } finally {
                graphics.dispose()
            }
        }
        val frame = BufferedImageFrameData(image, dimensions(4, 3))

        val encoded = frame.toByteArray()
        val decoded = assertNotNull(ImageIO.read(ByteArrayInputStream(encoded)))

        assertTrue(encoded.size > 4)
        assertEquals(0xFF.toByte(), encoded[0])
        assertEquals(0xD8.toByte(), encoded[1])
        assertEquals(4, decoded.width)
        assertEquals(3, decoded.height)
    }

    /** Verifies that metadata copying shares the image and preserves the original metadata. */
    @Test
    fun copyWithModifiedMetaKeepsImageAndDoesNotChangeOriginalMeta() {
        val image = BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB)
        val original = BufferedImageFrameData(image, dimensions(2, 2))

        val copy = original.copyWithModifiedMeta {
            put(SourceFrameSourceWidth, 8)
            put(SourceFrameSourceHeight, 6)
        } as BufferedImageFrameData

        assertNotSame(original, copy)
        assertSame(image, copy.bufferedImage)
        assertEquals(2, original.meta[SourceFrameSourceWidth])
        assertEquals(2, original.meta[SourceFrameSourceHeight])
        assertEquals(8, copy.meta[SourceFrameSourceWidth])
        assertEquals(6, copy.meta[SourceFrameSourceHeight])
    }

    /** Builds the dimension metadata used by test frames. */
    private fun dimensions(width: Int, height: Int) = buildMetaContainer {
        put(SourceFrameSourceWidth, width)
        put(SourceFrameSourceHeight, height)
    }
}
