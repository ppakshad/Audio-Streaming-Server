# Audio Streaming Server

A **multi-threaded audio streaming server** in **Java** using **TCP**. The client requests an audio file, and the server streams it in real-time.

## Features
- **Multi-threaded server** supporting multiple clients  
- **Real-time audio streaming** over TCP  
- **Playback controls** (Play, Pause, Resume, Stop)  
- **Elapsed time tracking** during playback  

## Installation & Execution
### **Requirements**
- **JDK 8+**
- **Java Sound API**

### **Steps to Run**
```sh
# Clone the repository
git clone https://github.com/ppakshad/Audio-Streaming-Server.git
cd audio-streaming-server

# Compile and run
javac Server.java Client.java
java Server  # Start the server
java Client  # Start the client
```

## Client Controls
| Command  | Action  |
|----------|---------|
| `Play`   | Start streaming |
| `Pause`  | Pause playback |
| `Resume` | Resume playback |
| `Stop`   | Stop streaming |
| `Exit`   | Disconnect |

## Notes
- Server listens on **port 3333**.
- Audio files must be **on the server’s disk**.
- Clients must request **valid filenames**.

## License
MIT License.
