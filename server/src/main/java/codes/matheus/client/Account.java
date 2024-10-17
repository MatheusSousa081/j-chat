package codes.matheus.client;

import codes.matheus.Username;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Account {
    private @NotNull Username username;
    private @Nullable Client client;

    public Account(@NotNull Username username) {
        this.username = username;
    }

    public Account() {
        this.username = new Username("");
    }

    public @NotNull Username getValue() {
        return username;
    }

    public void setUsername(@NotNull Username username) {
        this.username = username;
    }

    public void setUsername(@NotNull String username) {
        this.username = Username.parse(username);
    }

    public @Nullable Client getClient() {
        return client;
    }

    public void setClient(@Nullable Client client) {
        this.client = client;
    }

    public boolean isOnline() {
        return client != null;
    }
}
