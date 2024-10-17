package codes.matheus;

import codes.matheus.client.Account;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class Message {
    private final @NotNull Account account;
    private final @NotNull String message;
    private final @NotNull Instant instant;

    public Message(@NotNull Account account, @NotNull String message) {
        this.account = account;
        this.message = message;
        this.instant = Instant.now();
    }

    public @NotNull Account getAccount() {
        return account;
    }

    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull Instant getInstant() {
        return instant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(account, message1.account) && Objects.equals(message, message1.message) && Objects.equals(instant, message1.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, message, instant);
    }

    @Override
    public String toString() {
        ZonedDateTime brazilTime = instant.atZone(ZoneId.of("America/Sao_Paulo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return brazilTime.format(formatter) + " " + account.getValue() + ": " + message;
    }
}
