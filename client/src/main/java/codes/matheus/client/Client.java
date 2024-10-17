package codes.matheus.client;

import codes.matheus.Username;
import com.jlogm.Logger;
import com.jlogm.factory.LoggerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public final class Client {
    public static final @NotNull Logger log = LoggerFactory.getInstance().create(Client.class.getName());

    private final @NotNull SocketChannel socket;
    private final @NotNull Selector selector;

    public Client() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter your username: ");
            @NotNull Username username = Username.parse(reader.readLine());
            this.socket = SocketChannel.open(new InetSocketAddress("localhost", 12345));
            socket.configureBlocking(false);
            this.selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.wrap(username.getUsername().getBytes());
            socket.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        log.info("Connected to the server!");
        new Thread(() -> {
            try {
                while (socket.isOpen() && selector.isOpen()) {
                    @NotNull Iterator<@NotNull SelectionKey> keyIterator;
                    selector.select();
                    keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isReadable()) {
                            @NotNull SocketChannel channel = (SocketChannel) key.channel();
                            @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);

                            int bytesRead = channel.read(buffer);
                            if (bytesRead == -1) {
                                log.info("Server closed the connection");
                                channel.close();
                            } else {
                                buffer.flip();
                                String message = new String(buffer.array()).trim();
                                System.out.println(message);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (socket.isOpen() && selector.isOpen()) {
                    String message = reader.readLine();
                    if (!message.isBlank()) {
                        @NotNull ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                        socket.write(buffer);
                        if (message.equals("quit")) {
                            socket.close();
                            break;
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
