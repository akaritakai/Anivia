package me.parted.anivia.irc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IrcMessageReaderTest {

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
    public void testRead() throws Exception {
        assertEquals(noTags, IrcMessageReader.read(":SomeNick!someuser@some.client.fqdn SOMECMD :Some params\r\n"));
        assertEquals(withTags, IrcMessageReader.read("@firstKey=firstValue;keyAlone;lastKey=lastValue :SomeNick!someuser@some.client.fqdn SOMECMD :Some params\r\n"));
        assertEquals(noPrefix, IrcMessageReader.read("@firstKey=firstValue;keyAlone;lastKey=lastValue SOMECMD :Some params\r\n"));
        assertEquals(noParam, IrcMessageReader.read("@firstKey=firstValue;keyAlone;lastKey=lastValue :SomeNick!someuser@some.client.fqdn SOMECMD\r\n"));
    }
}