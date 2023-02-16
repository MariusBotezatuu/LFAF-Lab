package automaton;

public class Transition {
    public char currentState;
    public char nextState;
    public char transitionLabel;

    public Transition(char currentState, char nextState, char transitionLabel) {
        this.currentState = currentState;
        this.nextState = nextState;
        this.transitionLabel = transitionLabel;
    }
}
