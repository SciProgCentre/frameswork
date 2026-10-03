package space.kscience.frameswork.features.frames.common.models

import dev.inmo.micro_utils.meta.buildMetaContainer
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/** Tests the payload and metadata-copy semantics of [ByteArrayFrameData]. */
class ByteArrayFrameDataTests {
    /** Verifies that byte-backed frames return the original payload reference. */
    @Test
    fun toByteArrayReturnsOriginalPayload() = runTest {
        val payload = byteArrayOf(0, 1, -1, 127)
        val frame = ByteArrayFrameData(payload, MetaContainerFixtures.withDimensions(320, 240))

        assertSame(payload, frame.toByteArray())
    }

    /** Verifies that metadata copying preserves the payload and leaves the original metadata unchanged. */
    @Test
    fun copyWithModifiedMetaKeepsPayloadAndDoesNotChangeOriginalMeta() {
        val payload = byteArrayOf(1, 2, 3)
        val original = ByteArrayFrameData(payload, MetaContainerFixtures.withDimensions(320, 240))

        val copy = original.copyWithModifiedMeta {
            put(SourceFrameSourceWidth, 640)
            put(SourceFrameSourceHeight, 480)
        } as ByteArrayFrameData

        assertNotSame(original, copy)
        assertSame(payload, copy.bytes)
        assertEquals(320, original.meta[SourceFrameSourceWidth])
        assertEquals(240, original.meta[SourceFrameSourceHeight])
        assertEquals(640, copy.meta[SourceFrameSourceWidth])
        assertEquals(480, copy.meta[SourceFrameSourceHeight])
    }
}

/** Shared metadata fixtures for common frame-model tests. */
internal object MetaContainerFixtures {
    /** Builds metadata containing the supplied frame [width] and [height]. */
    fun withDimensions(width: Int, height: Int) = buildMetaContainer {
        put(SourceFrameSourceWidth, width)
        put(SourceFrameSourceHeight, height)
    }
}
