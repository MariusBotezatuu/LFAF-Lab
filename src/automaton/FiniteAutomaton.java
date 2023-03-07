package automaton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import grammar.Grammar;

public class FiniteAutomaton {
    private char[] possibleStates;
    public char[] alphabet;
    public Transition[] transitions;
    private char initialState;
    public char finalState;

    public String[] possibleStatesWithString;

    public FiniteAutomaton(char[] possibleStates, char[] alphabet, char initialState, char finalState,
            Transition[] transitions) {
        this.possibleStates = possibleStates.clone();
        this.alphabet = alphabet.clone();
        this.transitions = transitions.clone();
        this.initialState = initialState;
        this.finalState = finalState;
    }

    public FiniteAutomaton(String[] possibleStatesWithString, char[] alphabet, char initialState, char finalState,
            Transition[] transitions) {
        this.possibleStatesWithString = possibleStatesWithString.clone();
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

    public Grammar toRegularGrammar() {
        char[] nonTerminalVariables = Arrays.copyOf(possibleStates, possibleStates.length - 1);
        char[] terminalVariables = alphabet.clone();
        String rightSide;
        HashMap<String, String> productionRules = new HashMap<String, String>();
        for (int i = 0; i < transitions.length; i++) {
            String leftSide = String.valueOf(transitions[i].currentState) + (i + 1);
            if (i != 5)
                rightSide = String.valueOf(transitions[i].transitionLabel) + String.valueOf(transitions[i].nextState);
            else
                rightSide = String.valueOf(transitions[i].transitionLabel);

            productionRules.put(leftSide, rightSide);
        }

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productionRules);
        return grammar;
    }

    public boolean determineType() {
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].transitionLabel == 'ε')
                return false;
            for (int j = 0; j < transitions.length; j++) {
                if (i != j) {
                    if (transitions[j].currentState == transitions[i].currentState
                            && transitions[j].transitionLabel == transitions[i].transitionLabel
                            && transitions[j].nextState != transitions[i].nextState)
                        return false;
                }
            }
        }

        int count = 0;
        for (int i = 0; i < possibleStates.length; i++) {
            count = 0;
            for (int j = 0; j < alphabet.length; j++) {
                for (int z = 0; z < transitions.length; z++) {
                    if (transitions[z].currentState == possibleStates[i]
                            && transitions[z].transitionLabel == alphabet[j]) {
                        count++;
                        break;
                    }
                }
            }
            if (count != alphabet.length)
                return false;
        }
        return true;
    }

    public FiniteAutomaton toDeterministic() {
        ArrayList<Transition> DFAtransitions = new ArrayList<Transition>();
        ArrayList<String> DFApossibleStates = new ArrayList<String>();
        int index = 0;
        for (int i = 0; i < transitions.length; i++) {
            DFAtransitions.add(transitions[i]);
        }
        if (determineType())
            System.out.println("The finite automata is not non deterministic!");
        else {
            while (index < DFAtransitions.size()) {
                String merge = "";
                for (int i = 0; i < DFAtransitions.size(); i++) {
                    if (index != i) {
                        if ((DFAtransitions.get(index).currentState == DFAtransitions.get(i).currentState
                                && DFAtransitions.get(index).transitionLabel == DFAtransitions.get(i).transitionLabel
                                && DFAtransitions.get(index).nextState != DFAtransitions.get(i).nextState)
                                || ((DFAtransitions.get(index).currentState2 == DFAtransitions.get(i).currentState2
                                        && DFAtransitions.get(index).transitionLabel == DFAtransitions
                                                .get(i).transitionLabel
                                        && DFAtransitions.get(index).nextState2 != DFAtransitions
                                                .get(i).nextState2)
                                        && (DFAtransitions.get(index).currentState2 != null
                                                && DFAtransitions.get(index).nextState2 != null))) {
                            if (DFAtransitions.get(index).nextState2 != null)
                                merge = merge + DFAtransitions.get(index).nextState2;
                            else
                                merge = merge + DFAtransitions.get(index).nextState;

                            if (DFAtransitions.get(i).nextState2 != null)
                                merge = merge + DFAtransitions.get(i).nextState2;
                            else
                                merge = merge + DFAtransitions.get(i).nextState;
                            DFAtransitions.remove(i);
                        }
                    }
                }
                for (int j = 0; j < DFAtransitions.size(); j++) {
                    if (merge.contains(String.valueOf(DFAtransitions.get(j).currentState))) {
                        if (merge.contains(String.valueOf(DFAtransitions.get(j).nextState))) {
                            Transition DFAtransition = new Transition(merge, merge,
                                    DFAtransitions.get(j).transitionLabel);
                            DFAtransitions.set(j, DFAtransition);
                        } else {
                            Transition DFAtransition = new Transition(merge,
                                    String.valueOf(DFAtransitions.get(j).nextState),
                                    DFAtransitions.get(j).transitionLabel);
                            DFAtransitions.set(j, DFAtransition);
                        }
                    } else if (merge.contains(String.valueOf(DFAtransitions.get(j).nextState))) {
                        Transition DFAtransition = new Transition(
                                String.valueOf(DFAtransitions.get(j).currentState), merge,
                                DFAtransitions.get(j).transitionLabel);
                        DFAtransitions.set(j, DFAtransition);
                    }
                }
                index++;
            }
        }

        for (int i = 0; i < DFAtransitions.size(); i++) {
            if (DFAtransitions.get(i).currentState2 == null) {
                if (!DFApossibleStates.contains(String.valueOf(DFAtransitions.get(i).currentState))) {
                    DFApossibleStates.add(String.valueOf(DFAtransitions.get(i).currentState));
                }
                if (!DFApossibleStates.contains(String.valueOf(DFAtransitions.get(i).nextState))) {
                    DFApossibleStates.add(String.valueOf(DFAtransitions.get(i).nextState));
                }
            } else {
                if (!DFApossibleStates.contains(String.valueOf(DFAtransitions.get(i).currentState2))) {
                    DFApossibleStates.add(String.valueOf(DFAtransitions.get(i).currentState2));
                }
                if (!DFApossibleStates.contains(String.valueOf(DFAtransitions.get(i).nextState2))) {
                    DFApossibleStates.add(String.valueOf(DFAtransitions.get(i).nextState2));
                }
            }
        }

        for (int i = 0; i < DFApossibleStates.size(); i++) {
            String str = new String(alphabet);
            for (int j = 0; j < DFAtransitions.size(); j++) {
                if (str.contains(String.valueOf(DFAtransitions.get(j).transitionLabel))
                        && ((String.valueOf(DFAtransitions.get(j).currentState).equals(DFApossibleStates.get(i)))
                                || DFAtransitions.get(j).currentState2 != null
                                        && (DFAtransitions.get(j).currentState2).equals(DFApossibleStates.get(i)))) {
                    String strNew = str.replace(String.valueOf(DFAtransitions.get(j).transitionLabel), "");
                    str = new String(strNew);
                }
            }

            if (str.length() > 0 && !DFApossibleStates.contains("E"))
                DFApossibleStates.add("E");

            for (int j = 0; j < str.length(); j++) {
                Transition DFAtransition = new Transition(DFApossibleStates.get(i), "E", str.charAt(j));
                DFAtransitions.add(DFAtransition);
            }
        }
        FiniteAutomaton automaton3 = new FiniteAutomaton(DFApossibleStates.toArray(new String[0]), alphabet,
                initialState, finalState, DFAtransitions.toArray(new Transition[DFAtransitions.size()]));
        return automaton3;
    }

    public String getTransitionsAsString() {
        String automata = "";
        for (int i = 0; i < transitions.length; i++) {
            if (i != 0)
                automata += ";";
            if (transitions[i].currentState2 == null) {
                automata += transitions[i].currentState + "->" + transitions[i].transitionLabel
                        + transitions[i].nextState;
            } else {
                automata += transitions[i].currentState2 + "->" + transitions[i].transitionLabel
                        + transitions[i].nextState2;
            }
        }
        return automata;
    }
}
