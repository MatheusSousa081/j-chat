package codes.matheus;

import codes.matheus.client.Client;
import org.jetbrains.annotations.NotNull;

public class ClientMain {
    public static void main(String[] args) {
        @NotNull Client client = new Client();
        client.connect();
    }
}