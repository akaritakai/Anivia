package me.parted.anivia.fsm;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

class Nfa {

    public final State startState;
    public final Set<State> acceptStates;

    public Nfa(Nfa nfa) {
        Map<State, State> newStates = new HashMap<>();
        for (State s : nfa.getStates()) {
            State newState = newStates.get(s);
            if (newState == null) {
                newState = new State();
                newStates.put(s, newState);
            }
        }
        for (Entry<State, State> entry : newStates.entrySet()) {
            State oldState = entry.getKey();
            State newState = entry.getValue();
            for (Entry<Character, State> transition : oldState.transitions.entries()) {
                char symbol = transition.getKey();
                State result = transition.getValue();
                newState.transitions.put(symbol, newStates.get(result));
            }
            newState.epsilonTransitions.addAll(oldState.epsilonTransitions.stream()
                    .map(newStates::get)
                    .collect(Collectors.toList()));
        }
        this.startState = newStates.get(nfa.startState);
        this.acceptStates = new HashSet<>();
        this.acceptStates.addAll(nfa.acceptStates.stream()
                .map(newStates::get)
                .collect(Collectors.toList()));
    }

    public Nfa() {
        this.startState = new State();
        this.acceptStates = new HashSet<>();
    }

    private Set<State> getStates() {
        Set<State> states = new HashSet<>();
        states.add(startState);

        Queue<State> queue = new LinkedList<>();
        queue.add(startState);
        while (!queue.isEmpty()) {
            State s = queue.poll();
            Set<State> targets = s.transitions.entries().stream()
                    .map(Entry::getValue)
                    .collect(Collectors.toSet());
            targets.addAll(s.epsilonTransitions);
            queue.addAll(targets.stream()
                    .filter(states::add)
                    .collect(Collectors.toList()));
        }

        return states;
    }

    /*
     * Reverses the NFA.
     */
    Nfa reverse() {
        Set<State> states = getStates();
        Map<State, State> newStates = new HashMap<>();
        for (State state : states) {
            newStates.put(state, new State());
        }

        for (State oldFromState : states) {
            State newToState = newStates.get(oldFromState);
            for (Entry<Character, State> entry : oldFromState.transitions.entries()) {
                State newFromState = newStates.get(entry.getValue());
                newFromState.transitions.put(entry.getKey(), newToState);
            }
            for (State oldToState : oldFromState.epsilonTransitions) {
                State newFromState = newStates.get(oldToState);
                newFromState.epsilonTransitions.add(newToState);
            }
        }

        Nfa result = new Nfa();
        result.startState.epsilonTransitions.addAll(this.acceptStates.stream()
                .map(newStates::get)
                .collect(Collectors.toList()));
        result.acceptStates.add(this.startState);

        return result;
    }

    /*
     * Provides a DFA.
     */
    private Dfa toDfa() {
        return new Dfa(this);
    }

    /*
     * Minimizes this NFA into a DFA.
     */
    public Dfa minimize() {
        return reverse().toDfa().reverse().toDfa();
    }
}
