package com.ghzawi.proxystoreagent.service

/**
 * Binary Frame Protocol for High-Performance Tunnel Data Transfer
 *
 * Frame Format:
 * ┌──────────┬───────────────────┬─────────────┐
 * │  1 byte  │     36 bytes      │   N bytes   │
 * │   Type   │    Stream ID      │    Data     │
 * └──────────┴───────────────────┴─────────────┘
 *
 * Frame Types:
 * - 0x01 = Data Frame (tunnel data)
 * - 0x02 = Close Frame (tunnel close)
 *
 * Stream ID: Full UUID with dashes, UTF-8 encoded (exactly 36 bytes)
 * - Example: "1a61b90b-18ab-4143-9893-b1ce94ef5d58"
 * - No truncation, no padding needed
 */
object BinaryFrameCodec {

    // Frame type constants
    const val BINARY_FRAME_DATA: Byte = 0x01
    const val BINARY_FRAME_CLOSE: Byte = 0x02

    // Protocol constants
    const val STREAM_ID_LENGTH = 36  // 36 bytes for full UUID with dashes
    const val HEADER_SIZE = 1 + STREAM_ID_LENGTH  // Type + StreamID

    // Buffer size optimizations
    const val MAX_BUFFER_SIZE = 256 * 1024  // 256KB for high throughput

    /**
     * Encodes data into a binary frame for transmission
     *
     * @param frameType Type of frame (DATA or CLOSE)
     * @param streamId Stream identifier (full UUID with dashes, 36 characters)
     * @param data Binary data payload (can be empty for CLOSE frames)
     * @return Binary frame ready for WebSocket transmission
     */
    fun encodeBinaryFrame(frameType: Byte, streamId: String, data: ByteArray = ByteArray(0)): ByteArray {
        // Validate UUID length (must be exactly 36 characters with dashes)
        if (streamId.length != 36) {
            throw IllegalArgumentException("Stream ID must be 36 characters (UUID with dashes)")
        }

        // Convert UUID string to UTF-8 bytes (will be exactly 36 bytes)
        val streamIdBytes = streamId.toByteArray(Charsets.UTF_8)
        if (streamIdBytes.size != STREAM_ID_LENGTH) {
            throw IllegalStateException("Stream ID UTF-8 encoding produced wrong size")
        }

        // Create frame: [type:1][streamID:36][data:N]
        val frame = ByteArray(HEADER_SIZE + data.size)
        frame[0] = frameType
        System.arraycopy(streamIdBytes, 0, frame, 1, STREAM_ID_LENGTH)
        if (data.isNotEmpty()) {
            System.arraycopy(data, 0, frame, HEADER_SIZE, data.size)
        }

        return frame
    }

    /**
     * Decodes a binary frame received from WebSocket
     *
     * @param frame Raw binary frame
     * @return Triple of (frameType, streamId, data) or null if invalid
     */
    fun decodeBinaryFrame(frame: ByteArray): Triple<Byte, String, ByteArray>? {
        if (frame.size < HEADER_SIZE) {
            return null
        }

        val frameType = frame[0]
        val streamIdBytes = frame.copyOfRange(1, HEADER_SIZE)

        // Decode full 36-byte stream ID (no null padding, full UUID with dashes)
        val streamId = String(streamIdBytes, Charsets.UTF_8)

        // Validate UUID format (should be 36 chars with dashes)
        if (streamId.length != 36) {
            return null
        }

        val data = if (frame.size > HEADER_SIZE) {
            frame.copyOfRange(HEADER_SIZE, frame.size)
        } else {
            ByteArray(0)
        }

        return Triple(frameType, streamId, data)
    }
}
