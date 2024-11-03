package jdbcdrivers.generic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Objects;

import jdbcdrivers.util.BufferOutputStream;
import jdbcdrivers.util.DriverUtil;

/**
 * Socket connection to database, makes sure that complete messages are sent over the network connection.
 */
final class DriverSocket implements AutoCloseable {

    private static final boolean DEBUG = Boolean.FALSE;

    private final SocketChannel socketChannel;
    private final BufferOutputStream bufferOutputStream;

    DriverSocket(int sendBufferSize, int receiveBufferSize) throws IOException {

        this.socketChannel = SocketChannel.open();

        this.bufferOutputStream = new BufferOutputStream(sendBufferSize) {

            @Override
            public void flush() throws IOException {

                sendMessage();
            }
        };

        socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, sendBufferSize);
        socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, receiveBufferSize);
    }

    /**
     * Connect to a server.
     *
     * @param hostName name of the host to connect to
     * @param port the port to connect to
     *
     * @throws IOException if connecting fails
     * @throws NullPointerException if {@code hostName} is {@code null}
     * @throws IllegalArgumentException if {@code port} is out of range for valid port numbers
     */
    void connect(String hostName, int port) throws IOException {

        Objects.requireNonNull(hostName);

        if (port < 0) {

            throw new IllegalArgumentException();
        }

        if (port > DriverUtil.MAX_UNSIGNED_SHORT) {

            throw new IllegalArgumentException();
        }

        if (DEBUG) {

            System.out.println("socket connect");
        }

        socketChannel.connect(new InetSocketAddress(hostName, port));
        socketChannel.finishConnect();

        if (DEBUG) {

            System.out.println("socket connected");
        }
    }

    /**
     * Get socket {@link InputStream}.
     *
     * @return an {@link InputStream} by which to receive data from the socket
     */
    InputStream getInputStream() {

        return Channels.newInputStream(socketChannel);
    }

    /**
     * Get an {@link OutputStream} to write data to.
     *
     * @return an {@link OutputStream} by which to write data
     */
    OutputStream getOutputStream() {

        return bufferOutputStream;
    }

    private void sendMessage() throws IOException {

        socketChannel.write(bufferOutputStream.getOutputByteBuffer());

        bufferOutputStream.reset();
    }

    @Override
    public void close() throws IOException {

        socketChannel.close();
    }
}
