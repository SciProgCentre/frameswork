package space.kscience.frameswork.features.frames.common.models.javacv

import dev.inmo.micro_utils.meta.buildMetaContainer
import kotlinx.coroutines.test.runTest
import org.bytedeco.javacv.Frame
import space.kscience.frameswork.features.frames.common.models.SourceFrameSourceHeight
import space.kscience.frameswork.features.frames.common.models.SourceFrameSourceWidth
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/** Tests byte extraction and metadata copying for [JavaCVFrameData]. */
class JavaCVFrameDataTests {
    /** Verifies that conversion returns the JavaCV frame data buffer's backing array. */
    @Test
    fun toByteArrayReturnsFrameDataPayload() = runTest {
        val payload = byteArrayOf(1, 0, -1, 127)
        val javaCvFrame = Frame().apply {
            data = ByteBuffer.wrap(payload)
        }
        val frame = JavaCVFrameData(javaCvFrame, dimensions(320, 240))

        assertSame(payload, frame.toByteArray())
    }

    /** Verifies that metadata copying shares the JavaCV frame and preserves the original metadata. */
    @Test
    fun copyWithModifiedMetaKeepsFrameAndDoesNotChangeOriginalMeta() {
        val javaCvFrame = Frame()
        val original = JavaCVFrameData(javaCvFrame, dimensions(320, 240))

        val copy = original.copyWithModifiedMeta {
            put(SourceFrameSourceWidth, 640)
            put(SourceFrameSourceHeight, 480)
        } as JavaCVFrameData

        assertNotSame(original, copy)
        assertSame(javaCvFrame, copy.frame)
        assertEquals(320, original.meta[SourceFrameSourceWidth])
        assertEquals(240, original.meta[SourceFrameSourceHeight])
        assertEquals(640, copy.meta[SourceFrameSourceWidth])
        assertEquals(480, copy.meta[SourceFrameSourceHeight])
    }

    /** Builds the dimension metadata used by test frames. */
    private fun dimensions(width: Int, height: Int) = buildMetaContainer {
        put(SourceFrameSourceWidth, width)
        put(SourceFrameSourceHeight, height)
    }
}
