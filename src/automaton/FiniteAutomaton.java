package automaton;

public class FiniteAutomaton {
    private char[] possibleStates;
    private char[] alphabet;
    private Transition[] transitions;
    private char initialState;
    private char finalState;

    public FiniteAutomaton(char[] possibleStates, char[] alphabet, char initialState, char finalState,
            Transition[] transitions) {
        this.possibleStates = possibleStates.clone();
        this.alphabet = alphabet.clone();
        this.transitions = transitions.clone();
        this.initialState = initialState;
        this.finalState = finalState;
    }

    public boolean stringBelongToLanguage(final String inputString) {
        char state = 'S';
        boolean currentSymbolExists = false;

        for (int j = 0; j < inputString.length() + 1; j++) {
            if ((!currentSymbolExists && j != 0) || (j == inputString.length() && state != 'F'))
                return false;
            else if (j == inputString.length())
                return true;

            currentSymbolExists = false;

            for (int i = 0; i < 9; i++) {
                if (state == transitions[i].currentState && transitions[i].transitionLabel == inputString.charAt(j)) {
                    currentSymbolExists = true;
                    state = transitions[i].nextState;
                    break;
                }
            }
        }
        return false;
    }
}
