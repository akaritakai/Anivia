package me.parted.anivia.model;

import me.parted.anivia.exception.AlreadyExistsException;
import me.parted.anivia.exception.InvalidGlobException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code GlobSet} class stores a set of glob-type regexes and provides public methods for retrieving, adding,
 * removing, and checking against them in an efficient manner.
 *
 * @author Justin Kaufman
 * @since 1.0
 */
public class GlobSet {

    private final Map<String, String> globToRegexMap;
    private Pattern pattern;
    private Matcher matcher;

    /**
     * Default constructor initializes an empty set.
     */
    public GlobSet() {
        this.globToRegexMap = new HashMap<>();
    }

    /**
     * Returns the list of globs that this GlobSet matches against.
     * @return a list of globs
     */
    @NotNull
    public List<String> getGlobList() {
        return new LinkedList<>(globToRegexMap.keySet());
    }

    /**
     * Returns true if a provided String matches any of the regexes in this set.
     * @param str the {@link java.lang.String} to test
     * @return true if a match is found
     */
    public boolean match(@Nullable String str) {
        if (str == null || pattern == null) {
            return false;
        }
        str = str.toLowerCase();
        if (matcher == null) {
            matcher = pattern.matcher(str);
            return matcher.matches();
        }
        return matcher.reset(str).matches();
    }

    /**
     * Adds a glob to this GlobSet.
     * @param glob the glob regex to add
     * @throws AlreadyExistsException if this regex is already present in the GlobSet
     * @throws InvalidGlobException if this regex is invalid
     */
    public void addGlob(@NotNull String glob) throws AlreadyExistsException, InvalidGlobException {
        glob = glob.toLowerCase();
        if (globToRegexMap.containsKey(glob)) {
            throw new AlreadyExistsException("The glob already exists in the GlobSet.");
        }

        // Test the glob before adding it to our cache.
        String regex = globToRegex(glob);
        try {
            //noinspection ResultOfMethodCallIgnored
            Pattern.compile(regex);
        } catch (Exception e) {
            throw new InvalidGlobException("The glob is invalid and cannot be compiled.");
        }

        globToRegexMap.put(glob, globToRegex(glob));
        compilePatterns();
    }

    /**
     * Removes a glob from this GlobSet.
     * @param glob the glob to remove
     */
    public void removeGlob(@NotNull String glob) {
        glob = glob.toLowerCase();
        globToRegexMap.remove(glob);
        try {
            compilePatterns();
        } catch (InvalidGlobException e) {
            // We shouldn't ever get this error, so log it, but continue execution.
            // TODO logging
        }
    }

    /**
     * Concatenates all of the patterns currently in our glob-to-regex map into a single compiled pattern.
     * @throws InvalidGlobException if the compilation of the pattern fails
     */
    private void compilePatterns() throws InvalidGlobException {
        StringBuilder sb = new StringBuilder();
        sb.append("^"); // regex beginning
        Iterator<String> it = globToRegexMap.values().iterator();
        while (it.hasNext()) {
            String regex = it.next();
            if (it.hasNext()) {
                sb.append("(").append(regex).append(")|");
            } else {
                sb.append("(").append(regex).append(")");
            }
        }
        sb.append("$"); // regex end
        try {
            pattern = Pattern.compile(sb.toString());
        } catch (Exception e) {
            // We shouldn't ever get this error since we check patterns before adding them, so log it
            // TODO logging
            throw new InvalidGlobException("The patterns in this cache could not be concatenated and compiled.");
        }
        matcher = null; // null out matcher so it will be recompiled on first use
    }

    /**
     * This method accepts a glob regex, quotes it for safety, and then replaces the wildcards '*' and '?' with '.*' and
     * '.' respectively.
     * @param glob the glob regex to convert
     * @return the equivalent Java-style regex
     */
    @NotNull
    private static String globToRegex(@NotNull String glob) {
        boolean escaped = false;
        int start = 0, end = 0;
        StringBuilder regex = new StringBuilder();
        for (char c : glob.toCharArray()) {
            switch (c) {
                case '\\':
                    escaped = !escaped;
                    break;
                case '*':
                    if (!escaped) {
                        regex.append(Pattern.quote(glob.substring(start, end)));
                        regex.append(".*");
                        start = ++end;
                    }
                    break;
                case '?':
                    if (!escaped) {
                        regex.append(Pattern.quote(glob.substring(start, end)));
                        regex.append(".");
                        start = ++end;
                    }
                    break;
                default:
                    ++end;
            }
        }
        regex.append(Pattern.quote(glob.substring(start, end)));
        return regex.toString();
    }

}
