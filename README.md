# ProxyStore Agent

An Android application that acts as a residential proxy agent, connecting devices to a proxy network server via WebSocket.

## Features

- **WebSocket Connection**: Real-time bidirectional communication with proxy server
- **HTTP Request Proxying**: Forwards HTTP/HTTPS requests through the device
- **TCP Tunneling**: Supports CONNECT method for TCP-level proxying
- **Binary Frame Protocol**: High-performance data transfer using binary WebSocket frames
- **Automatic Reconnection**: Handles network changes and reconnection attempts
- **Device Onboarding**: Secure pairing token-based device registration
- **Foreground Service**: Runs reliably in the background with notifications
- **Stats Tracking**: Monitors active connections and data transferred

## Architecture

### Key Components

- **ProxyService**: Foreground service managing WebSocket connection and proxy operations
- **ProxyViewModel**: UI state management and service coordination
- **BinaryFrameCodec**: Efficient binary protocol for data streaming (256KB buffers)
- **PrefsManager**: Encrypted storage for device credentials and settings

### Message Types

- `REQUEST`: HTTP request forwarding
- `CONNECT`: TCP tunnel establishment
- `DATA`: Legacy JSON-based data transfer
- `BINARY`: High-performance binary data frames
- `CLOSE`: Stream closure
- `WELCOME`: Device credentials from server
- `OFFBOARD`: Remote device removal

## Requirements

- Android 8.0 (API 26) or higher
- Internet connection
- Valid pairing token from proxy network server

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/ProxyStoreAgent.git
   cd ProxyStoreAgent
   ```

2. Open in Android Studio

3. Sync Gradle and build the project

4. Update the WebSocket server URL in `ProxyService.kt` if needed

## Configuration

The app connects to the WebSocket server at:
```
wss://proxystore.ghzawi.com/ws
```

To change the server URL, modify the `connectWebSocket()` function in [ProxyService.kt](app/src/main/java/com/ghzawi/proxystoreagent/service/ProxyService.kt).

## Usage

1. Launch the app
2. Enter the 6-digit pairing token provided by the proxy network
3. Tap "Connect"
4. The device will register and begin accepting proxy requests

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/`

## Security

- Device credentials stored in encrypted SharedPreferences
- Hardware ID based device identification
- Token-based authentication
- HTTPS/WSS encrypted communication

## Performance Optimizations

- Binary WebSocket frames (no Base64 encoding)
- 256KB buffer size for high-throughput data transfer
- Connection pooling and keep-alive
- Minimal logging in production builds

## License

[Your License Here]

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
