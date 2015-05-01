package me.parted.anivia.irc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class IrcPrefixTest {

    private final IrcPrefix serverPrefix = new IrcPrefix("server.with.fqdn");
    private final IrcPrefix clientPrefix = new IrcPrefix("SomeNick", "someuser", "some.client.fqdn");
    private final IrcPrefix noUserPrefix = new IrcPrefix("SomeNick", null, "some.client.fqdn");
    private final IrcPrefix noHostPrefix = new IrcPrefix("SomeNick", null, null);

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testNullServerName() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcPrefix(null);
    }

    @Test
    public void testEmptyServerName() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcPrefix("");
    }

    @Test
    public void testNullNickname() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcPrefix(null, "anything", "anything");
    }

    @Test
    public void testEmptyNickname() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcPrefix("", "anything", "anything");
    }

    @Test
    public void testEmptyUserToNull() throws Exception {
        IrcPrefix ircPrefix = new IrcPrefix("anything", "", "anything");
        assertEquals(null, ircPrefix.getUser());
    }

    @Test
    public void testEmptyHostToNull() throws Exception {
        IrcPrefix ircPrefix = new IrcPrefix("anything", "anything", "");
        assertEquals(null, ircPrefix.getHost());
    }

    @Test
    public void testGetServerName() throws Exception {
        assertEquals("server.with.fqdn", serverPrefix.getServerName());
        assertEquals(null, clientPrefix.getServerName());
    }

    @Test
    public void testGetNickname() throws Exception {
        assertEquals(null, serverPrefix.getNickname());
        assertEquals("SomeNick", clientPrefix.getNickname());
    }

    @Test
    public void testGetUser() throws Exception {
        assertEquals(null, serverPrefix.getUser());
        assertEquals("someuser", clientPrefix.getUser());
    }

    @Test
    public void testGetHost() throws Exception {
        assertEquals(null, serverPrefix.getHost());
        assertEquals("some.client.fqdn", clientPrefix.getHost());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(":server.with.fqdn", serverPrefix.toString());
        assertEquals(":SomeNick!someuser@some.client.fqdn", clientPrefix.toString());
        assertEquals(":SomeNick@some.client.fqdn", noUserPrefix.toString());
        assertEquals(":SomeNick", noHostPrefix.toString());
    }
}