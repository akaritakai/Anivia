package me.parted.anivia.fsm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.HashSet;
import java.util.Set;

public class State {

    public final Multimap<Character, State> transitions = ArrayListMultimap.create();
    public final Set<State> epsilonTransitions = new HashSet<>();

}