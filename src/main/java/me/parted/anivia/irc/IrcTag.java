package me.parted.anivia.irc;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The {@code IrcTag} class represents an object version of an IRC Message Tag described in the IRCv3.2 specification.
 * <p>
 * <b>Note:</b> This class does not provide any validation guarantees.
 *
 * @author Justin Kaufman
 * @see me.parted.anivia.irc.IrcMessage
 * @since 1.0
 */
public class IrcTag {

    private final String key;
    private final String value;

    /**
     * Constructs a new {@code IrcTag} key-value pair.
     *
     * @param key   The key associated with this key-value pair, which may not be null or empty.
     * @param value The value associated with this key-value pair. If provided empty, it will be converted to null.
     */
    public IrcTag(@NotNull String key, @Nullable String value) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct an IrcTag from an empty key.");
        }
        this.key = key;
        this.value = Strings.emptyToNull(value);
    }

    /**
     * Returns the key associated with this {@code IrcTag}.
     *
     * @return A the key associated with this {@code IrcTag} guaranteed not to be null.
     */
    @NotNull
    public final String getKey() {
        return this.key;
    }

    /**
     * Returns the value associated with this {@code IrcTag}.
     *
     * @return A the value associated with this {@code IrcTag} which may be null.
     */
    @Nullable
    public final String getValue() {
        return this.value;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        IrcTag other = (IrcTag) obj;

        return Objects.equals(this.key, other.key)
                && Objects.equals(this.value, other.value);
    }

    @Override
    public final String toString() {
        if (value == null) {
            return key;
        }
        return key + "=" + value;
    }

}
