package me.parted.anivia.irc;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code IrcMessageReader} decodes a {@code String} containing the IRC protocol message as described in RFC 2812
 * Section 2.3 and in the IRCv3.2 specification into an {@link me.parted.anivia.irc.IrcMessage}.
 * <p>
 * The code in this class is generated through Ragel, a finite state machine compiler that compiles executable finite
 * state machines from regular languages. As a result, this code is not maintainable except from the Ragel-based input
 * file that contains rules on how to parse the protocol.
 *
 * @author Justin Kaufman
 * @see me.parted.anivia.irc.IrcMessage
 * @since 1.0
 */
public class IrcMessageReader {

    /**
     * Decodes a {@code String} containing a raw IRC protocol message as described in RFC 2812 Section 2.3 and in the
     * IRCv3.2 specification.
     * @param raw The raw line to be processed.
     * @return An {@code IrcMessage} containing the data processed from the raw line.
     */
    @Nullable
    public static IrcMessage read(String raw) {
        if (raw == null) {
            return null;
        }

        if (raw.length() > 1024) {
            return null;
        }
        /* Construction variables */
        IrcMessage ircMessage = null;

        List<IrcTag> ircTags = new ArrayList<>();
        IrcTag ircTag = null;
        String tagKey = null;
        String tagValue = null;

        IrcPrefix ircPrefix = null;
        String hostname = null;
        String nickname = null;
        String user = null;
        String host = null;

        String command = null;

        String params = null;

        /* Required tokens for the Ragel FSM */
        char[] data = raw.toCharArray();
        int cs, p = 0, pe = data.length, eof = data.length;
        int s = p; // current index
		int q = p; // section start

        %%{
            machine irc;

			action sectionstart { q = p; }
            action bufferstart { s = p; }
            action checksize {
                if (p - q > 512) {
                    return null;
                }
            }
            action tagkey {
                tagKey = new String(data, s, p - s);
            }
            action tagvalue {
                tagValue = new String(data, s, p - s);
            }
            action tag {
                ircTag = new IrcTag(tagKey, tagValue);
                ircTags.add(ircTag);
                tagValue = null; // the next tag could have a null value
            }
            action hostname {
                hostname = new String(data, s, p - s);
            }
            action host {
                host = new String(data, s, p - s);
            }
            action nickname {
                nickname = new String(data, s, p - s);
            }
            action user {
                user = new String(data, s, p - s);
            }
            action prefix {
                if (nickname == null) {
                    ircPrefix = new IrcPrefix(hostname);
                } else {
                    ircPrefix = new IrcPrefix(nickname, user, host);
                }
            }
            action command {
                command = new String(data, s, p - s);
            }
            action params {
                params = new String(data, s, p - s);
            }
            action message {
                ircMessage = new IrcMessage(ircTags, ircPrefix, command, params);
            }

            decoctet    = digit
                | ( ('1'..'9') digit )
                | ( '1' digit digit )
                | ( '2' ('0'..'4') digit )
                | ( '25' ('0'..'5') );
            ip4addr     = decoctet '.' decoctet '.' decoctet '.' decoctet;
            h16         = xdigit{1,4};
            ls32        = ( h16 ":" h16 ) | ip4addr;
            ip6addr     =                   ( ( h16 ':' ){6} ls32 )
                |                      ( '::' ( h16 ':' ){5} ls32 )
                |             ( ( h16 )? '::' ( h16 ':' ){4} ls32 )
                | ( ((h16 ':'){,1} h16)? '::' ( h16 ':' ){3} ls32 )
                | ( ((h16 ':'){,2} h16)? '::' ( h16 ':' ){2} ls32 )
                | ( ((h16 ':'){,3} h16)? '::'   h16 ':'      ls32 )
                | ( ((h16 ':'){,4} h16)? '::'                ls32 )
                | ( ((h16 ':'){,5} h16)? '::'                h16  )
                | ( ((h16 ':'){,6} h16)? '::'                     );
            hostaddr   = ip4addr | ip6addr;
            shortname  = alnum ( (alnum | '-')* alnum )?;
            hostname   = ( shortname ('.' shortname)* ) >bufferstart %hostname;
            host       = ( hostname | hostaddr ) >bufferstart %host;
            vendor     = hostname;
            tagvalue   = (extend - [\0\r\n; ])+ >bufferstart %tagvalue;
            tagkey     = ( (vendor '/')? (alpha | digit | '-')+ ) >bufferstart %tagkey;
            tag        = ( tagkey ('=' tagvalue)? ) %tag;
            tags       = ( tag ( ';' tag )* );
            user       = ( extend - [\0\r\n @] )+ >bufferstart %user;
            special    = ( 0x5B..0x60 ) | ( 0x7B..0x7D );
            nickname   = ( (alpha | special) (alnum | special | '-' ){,29} ) >bufferstart %nickname;
            prefix     = ( hostname | (nickname (("!" user)? "@" host)?) ) %prefix;
            command    = ( alpha+ | digit{3} ) >bufferstart %command;
            nospcrlfcl = extend - [\0\r\n :];
            middle     = nospcrlfcl ( ':' | nospcrlfcl )*;
            trailing   = ( ':' | ' ' | nospcrlfcl )*;
            params     = ( (' ' middle){,14} (' :' trailing)? ) >bufferstart %params;
            crlf       = '\r\n';
            message1   = ( '@' tags ' ' )? >sectionstart $checksize;
            message2   = ( ':' prefix ' ' )? command ( params )? crlf >sectionstart $checksize;
            message    = message1 message2;
            main      := message %message;

            write init;
            write exec;
        }%%

        return ircMessage;
    }

    %% write data;

}
