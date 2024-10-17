package codes.matheus.client;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public final class Client {
    private final @NotNull Account account;
    private final @NotNull SocketChannel socket;

    public Client(@NotNull Account account, @NotNull SocketChannel socket) {
        this.account = account;
        this.socket = socket;
    }

    public @NotNull Account getAccount() {
        return account;
    }

    public @NotNull SocketChannel getSocket() {
        return socket;
    }

    public @NotNull String read() throws IOException {
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);

        buffer.clear();
        int bytesRead = socket.read(buffer);
        if (bytesRead == -1) {
            socket.close();
            return "";
        }
        buffer.flip();
        return new String(buffer.array()).trim();
    }

    public void write(@NotNull String message) {
        @NotNull ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        try {
            socket.write(buffer);
            buffer.rewind();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(account, client.account) && Objects.equals(socket, client.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, socket);
    }
}
