package codes.matheus.server;

import codes.matheus.client.Account;
import codes.matheus.Message;
import codes.matheus.Username;
import codes.matheus.client.Client;
import com.jlogm.Logger;
import com.jlogm.factory.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Server {
    public static @NotNull Logger log = LoggerFactory.getInstance().create(Server.class.getName());

    private final @NotNull Set<@NotNull Client> clients = ConcurrentHashMap.newKeySet();
    private final @NotNull ServerSocketChannel server;
    private final @NotNull Selector selector;

    public Server() {
        try {
            this.server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress("localhost", 12345));
            server.configureBlocking(false);
            this.selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        new Thread(() -> {
            try {
                log.info("Server running on address " + server.getLocalAddress());

                while (server.isOpen() && selector.isOpen()) {
                    @NotNull Iterator<@NotNull SelectionKey> keyIterator;
                    selector.select();
                    keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isAcceptable()) {
                            @Nullable SocketChannel socket = server.accept();
                            if (socket != null) {
                                socket.configureBlocking(false);
                                log.info("New client joined the server!");
                                socket.register(selector, SelectionKey.OP_READ);
                            }
                        }

                        if (key.isReadable()) {
                            @NotNull SocketChannel socket = (SocketChannel) key.channel();
                            @Nullable Client sender = clients.stream()
                                    .filter(client1 -> client1.getSocket().equals(socket))
                                    .findFirst().orElse(null);

                            if (sender == null) {
                                @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);
                                int bytesRead = socket.read(buffer);
                                if (bytesRead == -1) {
                                    socket.close();
                                } else {
                                    buffer.flip();
                                    String text = new String(buffer.array()).trim();
                                    @NotNull Username username = new Username(text);
                                    @NotNull Client client = new Client(new Account(username), socket);
                                    client.getAccount().setClient(client);
                                    clients.add(client);
                                    log.info(username + " joined the server!");
                                    broadcast(username + " joined the chat");
                                    socket.register(selector, SelectionKey.OP_READ);
                                }
                            } else {
                                String text = sender.read();
                                if (!text.isBlank()) {
                                    @NotNull Message message = new Message(sender.getAccount(), text);
                                    System.out.println(message);
                                    broadcast(message.toString(), sender);
                                } else {
                                    socket.close();
                                    clients.remove(sender);
                                    log.info("Client disconnected");
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void broadcast(@NotNull String message, @NotNull Client sender) {
        for (@NotNull Client client : clients) {
            if (sender != client) {
                client.write(message);
            }
        }
    }

    public void broadcast(@NotNull String message) {
        for (@NotNull Client client : clients) {
            client.write(message);
        }
    }
}
