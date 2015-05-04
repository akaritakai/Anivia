package me.parted.anivia.fsm;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Dfa extends Nfa {

    private State currentState = startState;
    private final Map<Entry<State, Character>, State> transitions = new HashMap<>();

    /*
     * constructs a DFA. from the NFA.
     */
    public Dfa(Nfa nfa) {
        super();
        determinize(nfa);
    }

    public void consume(char c) {
        if (currentState == null) {
            return;
        }
        currentState = transitions.get(new SimpleImmutableEntry<>(currentState, c));
    }

    public boolean accept() {
        return acceptStates.contains(currentState);
    }

    private void determinize(Nfa nfa) {
        Map<NfaStateSet, State> dfaStates = new HashMap<>();
        Queue<NfaStateSet> queue = new LinkedList<>();

        //noinspection MismatchedQueryAndUpdateOfCollection
        NfaStateSet startSet = new NfaStateSet(this.startState); // this is updated with followEpsilons()
        this.startState.epsilonTransitions.add(nfa.startState);
        startSet.followEpsilons();
        dfaStates.put(startSet, this.startState);
        queue.add(startSet);

        while (!queue.isEmpty()) {
            NfaStateSet nfaFromStates = queue.poll();
            State dfaFromState = dfaStates.get(nfaFromStates);
            Map<Character, NfaStateSet> dfaTransitions = new HashMap<>();
            for (State nfaFromState : nfaFromStates) {
                if (nfa.acceptStates.contains(nfaFromState)) {
                    this.acceptStates.add(dfaFromState);
                }
                for (Entry<Character, State> entry : nfaFromState.transitions.entries()) {
                    NfaStateSet nfaTargetStates = dfaTransitions.get(entry.getKey());
                    if (nfaTargetStates == null) {
                        nfaTargetStates = new NfaStateSet();
                        dfaTransitions.put(entry.getKey(), nfaTargetStates);
                    }
                    nfaTargetStates.add(entry.getValue());
                }
            }
            for (Entry<Character, NfaStateSet> entry : dfaTransitions.entrySet()) {
                char symbol = entry.getKey();
                NfaStateSet nfaToStates = entry.getValue();
                nfaToStates.followEpsilons();
                State dfaToState = dfaStates.get(nfaToStates);
                if (dfaToState == null) {
                    dfaToState = new State();
                    dfaStates.put(nfaToStates, dfaToState);
                    queue.add(nfaToStates);
                }
                dfaFromState.transitions.put(symbol, dfaToState);
            }
        }

        for (State state : dfaStates.values()) {
            for (Entry<Character, State> entry : state.transitions.entries()) {
                char symbol = entry.getKey();
                State target = entry.getValue();
                transitions.put(new SimpleImmutableEntry<>(state, symbol), target);
            }
        }
    }

    private class NfaStateSet extends HashSet<State> {
        NfaStateSet(State... states) {
            this.addAll(Arrays.asList(states));
        }
        public void followEpsilons() {
            Queue<State> queue = new LinkedList<>();
            queue.addAll(this);
            while (!queue.isEmpty()) {
                State s = queue.poll();
                queue.addAll(s.epsilonTransitions.stream()
                        .filter(this::add)
                        .collect(Collectors.toList()));
            }
        }
    }

}
