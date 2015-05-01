package me.parted.anivia.irc;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * The {@code IrcMessage} class represents an object version of the IRC message protocol as described in RFC 2812
 * Section 2.3 and in the IRCv3.2 specification.
 * <p>
 * <b>Note:</b> This class does not provide any validation guarantees.
 *
 * @author Justin Kaufman
 * @see me.parted.anivia.irc.IrcMessageReader
 * @see me.parted.anivia.irc.IrcMessageWriter
 * @since 1.0
 */
public class IrcMessage {

    private final List<IrcTag> ircTags;
    private final IrcPrefix ircPrefix;
    private final String command;
    private final String params;

    /**
     * Constructs a new {@code IrcMessage} from the given data.
     *
     * @param ircTags   A list of IRC tags that this message contains.
     * @param ircPrefix The prefix form of the message as described in ABNF in RFC 2812 Section 2.3.1.
     * @param command   The command form of the message as described in ABNF in RFC 2812 Section 2.3.1.
     * @param params    The params form of the message as described in ABNF in RFC 2812 Section 2.3.1.
     */
    public IrcMessage(@NotNull List<IrcTag> ircTags, IrcPrefix ircPrefix, @NotNull String command, String params) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct an IrcMessage from an empty command.");
        }
        this.ircTags = ImmutableList.copyOf(ircTags);
        this.ircPrefix = ircPrefix;
        this.command = command;
        this.params = Strings.emptyToNull(params);
    }

    /**
     * Returns a list of IRC tags that this message contains.
     *
     * @return A list of IRC tags guaranteed not to be null.
     */
    @NotNull
    public List<IrcTag> getTags() {
        return this.ircTags;
    }

    /**
     * Returns an {@code IrcPrefix} object that this message contains.
     *
     * @return An {@code IrcPrefix} which may be null.
     */
    @Nullable
    public IrcPrefix getPrefix() {
        return this.ircPrefix;
    }

    /**
     * Returns the command associated with this {@code IrcMessage}.
     *
     * @return The command associated with this {@code IrcMessage} guaranteed not to be null.
     */
    @NotNull
    public String getCommand() {
        return this.command;
    }

    /**
     * Returns the params associated with this {@code IrcMessage}.
     *
     * @return The params associated with this {@code IrcMessage} which may be null.
     */
    @Nullable
    public String getParams() {
        return this.params;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ircTags, ircPrefix, command, params);
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
        IrcMessage other = (IrcMessage) obj;

        return Objects.equals(this.ircTags, other.ircTags)
                && Objects.equals(this.ircPrefix, other.ircPrefix)
                && Objects.equals(this.command, other.command)
                && Objects.equals(this.params, other.params);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (!ircTags.isEmpty()) {
            sb.append('@');
            sb.append(Joiner.on(';').join(ircTags));
            sb.append(' ');
        }

        if (ircPrefix != null) {
            sb.append(ircPrefix);
            sb.append(' ');
        }

        sb.append(command);

        if (!Strings.isNullOrEmpty(params)) {
            sb.append(params);
        }

        sb.append("\r\n");

        return sb.toString();
    }

}
