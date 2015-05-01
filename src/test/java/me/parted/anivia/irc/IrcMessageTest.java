package me.parted.anivia.irc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IrcMessageTest {

    private final IrcPrefix ircPrefix = new IrcPrefix("SomeNick", "someuser", "some.client.fqdn");
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private IrcMessage noTags = new IrcMessage(getEmptyIrcTagList(), ircPrefix, "SOMECMD", " :Some params");
    private IrcMessage withTags = new IrcMessage(getNonEmptyIrcTagList(), ircPrefix, "SOMECMD", " :Some params");
    private IrcMessage noPrefix = new IrcMessage(getNonEmptyIrcTagList(), null, "SOMECMD", " :Some params");
    private IrcMessage noParam = new IrcMessage(getNonEmptyIrcTagList(), ircPrefix, "SOMECMD", null);

    private List<IrcTag> getEmptyIrcTagList() {
        return new ArrayList<>();
    }

    private List<IrcTag> getNonEmptyIrcTagList() {
        ArrayList<IrcTag> list = new ArrayList<>();
        list.add(new IrcTag("firstKey", "firstValue"));
        list.add(new IrcTag("keyAlone", null));
        list.add(new IrcTag("lastKey", "lastValue"));
        return list;
    }

    @Test
    public void testNullTagList() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcMessage(null, ircPrefix, "SOMECMD", " :Some params");
    }

    @Test
    public void testNullCommand() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcMessage(getNonEmptyIrcTagList(), ircPrefix, null, " :Some params");
    }

    @Test
    public void testEmptyCommand() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcMessage(getNonEmptyIrcTagList(), ircPrefix, "", " :Some params");
    }

    @Test
    public void testGetTags() throws Exception {
        assertEquals(getEmptyIrcTagList(), noTags.getTags());
        assertEquals(getNonEmptyIrcTagList(), withTags.getTags());
    }

    @Test
    public void testGetPrefix() throws Exception {
        assertEquals(ircPrefix, withTags.getPrefix());
        assertEquals(null, noPrefix.getPrefix());
    }

    @Test
    public void testGetCommand() throws Exception {
        assertEquals("SOMECMD", withTags.getCommand());
    }

    @Test
    public void testGetParams() throws Exception {
        assertEquals(" :Some params", withTags.getParams());
        assertEquals(null, noParam.getParams());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(":SomeNick!someuser@some.client.fqdn SOMECMD :Some params\r\n", noTags.toString());
        assertEquals("@firstKey=firstValue;keyAlone;lastKey=lastValue :SomeNick!someuser@some.client.fqdn SOMECMD :Some params\r\n", withTags.toString());
        assertEquals("@firstKey=firstValue;keyAlone;lastKey=lastValue SOMECMD :Some params\r\n", noPrefix.toString());
        assertEquals("@firstKey=firstValue;keyAlone;lastKey=lastValue :SomeNick!someuser@some.client.fqdn SOMECMD\r\n", noParam.toString());
    }
}