package me.parted.anivia.irc;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The {@code IrcPrefix} class represents an object version of an IRC message prefix as described in ABNF in RFC 2812
 * Section 2.3.1.
 * <p>
 * <b>Note:</b> This class does not provide any validation guarantees.
 *
 * @author Justin Kaufman
 * @see me.parted.anivia.irc.IrcMessage
 * @since 1.0
 */
public class IrcPrefix {
    private final String serverName;
    private final String nickname;
    private final String user;
    private final String host;

    /**
     * Constructs a new {@code IrcPrefix} representing a server-based origin.
     *
     * @param serverName A serverName that this prefix contains which may not be null.
     */
    public IrcPrefix(@NotNull String serverName) {
        if (serverName.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct an IrcPrefix from an empty serverName.");
        }
        this.serverName = serverName;
        this.nickname = null;
        this.user = null;
        this.host = null;
    }


    /**
     * Constructs a new {@code IrcPrefix} representing a client-based origin.
     *
     * @param nickname A nickname that this prefix contains which may not be null.
     * @param user     A nickname that this prefix contains
     * @param host     A host that this prefix contains.
     */
    public IrcPrefix(@NotNull String nickname, @Nullable String user, @Nullable String host) {
        if (nickname.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct an IrcPrefix from an empty nickname.");
        }
        this.serverName = null;
        this.nickname = nickname;
        this.user = Strings.emptyToNull(user);
        this.host = Strings.emptyToNull(host);
    }

    /**
     * Returns the server name associated with this {@code IrcPrefix}.
     *
     * @return A possibly null String containing the server name.
     */
    @Nullable
    public String getServerName() {
        return this.serverName;
    }

    /**
     * Returns the nickname associated with this {@code IrcPrefix}.
     *
     * @return A possibly null String containing the nickname.
     */
    @Nullable
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Returns the user associated with this {@code IrcPrefix}.
     *
     * @return A possibly null String containing the user.
     */
    @Nullable
    public String getUser() {
        return this.user;
    }

    /**
     * Returns the host associated with this {@code IrcPrefix}.
     *
     * @return A possibly null String containing the host.
     */
    @Nullable
    public String getHost() {
        return this.host;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverName, nickname, user, host);
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
        IrcPrefix other = (IrcPrefix) obj;

        return Objects.equals(this.serverName, other.serverName)
                && Objects.equals(this.nickname, other.nickname)
                && Objects.equals(this.user, other.user)
                && Objects.equals(this.host, other.host);
    }

    @Override
    public String toString() {
        if (Strings.isNullOrEmpty(nickname)) {
            return ":" + serverName;
        }
        if (Strings.isNullOrEmpty(user)) {
            if (Strings.isNullOrEmpty(host)) {
                return ":" + nickname;
            }
            return ":" + nickname + "@" + host;
        }
        return ":" + nickname + "!" + user + "@" + host;

    }
}
