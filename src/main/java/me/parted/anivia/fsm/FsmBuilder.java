package me.parted.anivia.fsm;

public class FsmBuilder {

    private final Nfa nfa;

    public FsmBuilder() {
        this.nfa = new Nfa();
    }

    private FsmBuilder(Nfa nfa) {
        this.nfa = new Nfa(nfa);
    }

    /*
     * This NFA or the other NFA.
     */
    public FsmBuilder or(FsmBuilder other) {
        other = new FsmBuilder(other.nfa);

        Nfa newNfa = new Nfa();

        newNfa.startState.epsilonTransitions.add(this.nfa.startState);
        newNfa.startState.epsilonTransitions.add(other.nfa.startState);
        newNfa.acceptStates.addAll(this.nfa.acceptStates);
        newNfa.acceptStates.addAll(other.nfa.acceptStates);

        return new FsmBuilder(newNfa);
    }

    /*
     * This NFA once or not at all.
     */
    public FsmBuilder choice() {
        Nfa newNfa = new Nfa();

        newNfa.acceptStates.add(newNfa.startState);
        newNfa.acceptStates.addAll(this.nfa.acceptStates);
        newNfa.startState.epsilonTransitions.add(this.nfa.startState);

        return new FsmBuilder(newNfa);
    }

    /*
     * This NFA zero or more times.
     */
    public FsmBuilder star() {
        Nfa newNfa = new Nfa();

        newNfa.acceptStates.add(newNfa.startState);
        newNfa.acceptStates.addAll(this.nfa.acceptStates);
        for (State s : newNfa.acceptStates) {
            s.epsilonTransitions.add(this.nfa.startState);
        }

        return new FsmBuilder(newNfa);
    }

    /*
     * This NFA one or more times.
     */
    public FsmBuilder plus() {
        Nfa newNfa = new Nfa();

        newNfa.acceptStates.addAll(this.nfa.acceptStates);
        for (State s : newNfa.acceptStates) {
            s.epsilonTransitions.add(this.nfa.startState);
        }
        newNfa.startState.epsilonTransitions.add(this.nfa.startState);

        return new FsmBuilder(newNfa);
    }

    /*
     * This NFA followed by the other NFA.
     */
    public FsmBuilder followedBy(FsmBuilder other) {
        other = new FsmBuilder(other.nfa);

        if (this.nfa.acceptStates.isEmpty()) {
            this.nfa.startState.epsilonTransitions.add(other.nfa.startState);
        }
        else {
            for (State s : this.nfa.acceptStates) {
                s.epsilonTransitions.add(other.nfa.startState);
            }
        }
        this.nfa.acceptStates.clear();
        this.nfa.acceptStates.addAll(other.nfa.acceptStates);

        return new FsmBuilder(nfa);
    }

    /*
     * This NFA n times.
     */
    public FsmBuilder times(int n) {
        if (n == 0) {
            return new FsmBuilder(new Nfa());
        }
        else if (n == 1) {
            return new FsmBuilder(this.nfa);
        }
        else {
            return this.followedBy(this).times(n - 1);
        }
    }

    /*
     * This NFA at least n times and not more than m times.
     */
    public FsmBuilder times(int n, int m) {
        FsmBuilder builder = this;
        for(int i = n; i <= m; i++) {
            builder = builder.or(builder.times(i));
        }
        return new FsmBuilder(this.nfa);
    }

    /*
     * This NFA concatenated with one state for which there are a number of symbols.
     */
    public FsmBuilder followedBy(char[] symbols) {
        State end = new State();
        if (nfa.acceptStates.isEmpty()) {
            for (char c : symbols) {
                nfa.startState.transitions.put(c, end);
            }
        }
        else {
            for (State s : nfa.acceptStates) {
                for (char c : symbols) {
                    s.transitions.put(c, end);
                }
            }
        }
        nfa.acceptStates.clear();
        nfa.acceptStates.add(end);
        return this;
    }

    public Dfa build() {
        return nfa.minimize();
    }
}
