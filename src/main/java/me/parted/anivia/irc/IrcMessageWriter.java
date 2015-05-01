package me.parted.anivia.irc;

import org.jetbrains.annotations.Nullable;

/**
 * The {@code IrcMessageWriter} encodes an {@link me.parted.anivia.irc.IrcMessage} containing the IRC protocol message
 * as described in RFC 2812 Section 2.3 and in the IRCv3.2 specification into a raw {@code String} suitable for sending
 * over the wire.
 * <p>
 * <b>Note:</b> This class does not provide any validation guarantees.
 *
 * @author Justin Kaufman
 * @see me.parted.anivia.irc.IrcMessage
 * @since 1.0
 */
public class IrcMessageWriter {
    /**
     * Encodes an {@link me.parted.anivia.irc.IrcMessage} containing the IRC protocol message as described in RFC 2812
     * Section 2.3 and in the IRCv3.2 specification into a raw {@code String} suitable for sending over the wire.
     *
     * @param message The {@link me.parted.anivia.irc.IrcMessage} to be converted into a raw line.
     * @return A {@code String} containing the data in the {@link me.parted.anivia.irc.IrcMessage}.
     */
    @Nullable
    public static String write(IrcMessage message) {
        if (message == null) {
            return null;
        }
        return message.toString();
    }

}
