package codes.matheus;

import org.jetbrains.annotations.NotNull;

public final class Username implements CharSequence {
    public static boolean isValid(@NotNull String username) {
        if (username.isBlank()) {
            return false;
        } else if (!(username.length() >= 4 && username.length() <= 14)) {
            return false;
        } else if (!username.matches("^[a-zA-Z0-9_.-]+$")) {
            return false;
        }
        return true;
    }

    public static @NotNull Username parse(@NotNull String username) {
        if (!isValid(username)) {
            throw new IllegalArgumentException("Username invalid!");
        }
        return new Username(username);
    }

    private final @NotNull String username;

    public Username(@NotNull String username) {
        if (!isValid(username)) {
            throw new IllegalArgumentException("Username invalid!");
        }
        this.username = username;
    }

    public @NotNull String getUsername() {
        return username;
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int i) {
        return toString().charAt(i);
    }

    @Override
    public @NotNull CharSequence subSequence(int i, int i1) {
        return toString().subSequence(i, i1);
    }

    @Override
    public @NotNull String toString() {
        return username;
    }
}
