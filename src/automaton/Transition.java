package automaton;

public class Transition {
    public char currentState;
    public char nextState;
    public char transitionLabel;

    public String currentState2;
    public String nextState2;

    public Transition(char currentState, char nextState, char transitionLabel) {
        this.currentState = currentState;
        this.nextState = nextState;
        this.transitionLabel = transitionLabel;
    }

    public Transition(String currentState2, String nextState2, char transitionLabel) {
        this.currentState2 = currentState2;
        this.nextState2 = nextState2;
        this.transitionLabel = transitionLabel;
    }
}
