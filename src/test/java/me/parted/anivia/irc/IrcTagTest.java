package me.parted.anivia.irc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class IrcTagTest {

    private final IrcTag withValue = new IrcTag("beep", "boop");
    private final IrcTag withNoValue = new IrcTag("beep", null);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testNullKey() throws Exception {
        exception.expect(NullPointerException.class);
        new IrcTag(null, "anything");
    }

    @Test
    public void testEmptyKey() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new IrcTag("", "anything");
    }

    @Test
    public void testEmptyValueToNull() throws Exception {
        IrcTag ircTag = new IrcTag("anything", "");
        assertEquals(null, ircTag.getValue());
    }


    @Test
    public void testGetKey() throws Exception {
        assertEquals("beep", withValue.getKey());
        assertEquals("beep", withNoValue.getKey());
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals("boop", withValue.getValue());
        assertEquals(null, withNoValue.getValue());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("beep=boop", withValue.toString());
        assertEquals("beep", withNoValue.toString());
    }

}