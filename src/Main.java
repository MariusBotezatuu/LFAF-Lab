import java.util.HashMap;
import automaton.FiniteAutomaton;
import grammar.Grammar;
import automaton.Transition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // things for first lab

        HashMap<String, String> productionRules = new HashMap<String, String>();
        productionRules.put("S1", "aS");
        productionRules.put("S2", "bS");
        productionRules.put("S3", "cR");
        productionRules.put("S4", "dL");
        productionRules.put("R5", "dL");
        productionRules.put("R6", "e");
        productionRules.put("L7", "fL");
        productionRules.put("L8", "eL");
        productionRules.put("L9", "d");
        char[] nonTerminalVariables = { 'S', 'R', 'L' };
        char[] terminalVariables = { 'a', 'b', 'c', 'd', 'e', 'f' };
        String wordExample;
        ArrayList<String> wordExamples = new ArrayList<String>();

        Grammar grammar = new Grammar(nonTerminalVariables, terminalVariables, productionRules);

        while (wordExamples.size() < 5) {
            wordExample = grammar.generateString();
            grammar.releaseWord();
            if (!wordExamples.contains(wordExample)) {
                wordExamples.add(wordExample);
            }
        }
        System.out.println("Lab 1 Results\n5 strings by the given grammar: " + wordExamples);

        FiniteAutomaton automaton = grammar.toFiniteAutomaton();
        String wordToCheck = "deeeeeed";
        System.out.println(automaton.stringBelongToLanguage(wordToCheck)
                ? ("The string \"" + wordToCheck + "\" belongs to the language")
                : ("The string \"" + wordToCheck + "\" does not belong to the language"));

        // things for second lab

        System.out.println("\nLab 2 Results\nThe grammar from the previous lab is of " + grammar.classifyGrammar());

        // testing type 2 grammar

        /*
         * HashMap<String, String> productionRules2 = new HashMap<String, String>();
         * productionRules2.put("S1", "aSb");
         * productionRules2.put("S2", "baS");
         * productionRules2.put("S3", "cRdd");
         * productionRules2.put("S4", "dL");
         * productionRules2.put("R5", "aL");
         * productionRules2.put("L7", "f");
         * char[] nonTerminalVariables2 = { 'S', 'R', 'L' };
         * char[] terminalVariables2 = { 'a', 'b', 'c', 'd', 'f' };
         */

        // testing type 1 grammar

        /*
         * HashMap<String, String> productionRules2 = new HashMap<String, String>();
         * productionRules2.put("S1", "aSBC");
         * productionRules2.put("BC2", "b");
         * productionRules2.put("C3", "a");
         * productionRules2.put("B4", "b");
         * char[] nonTerminalVariables2 = { 'S', 'B', 'C' };
         * char[] terminalVariables2 = { 'a', 'b' };
         */

        // testing type 0 grammar

        /*
         * HashMap<String, String> productionRules2 = new HashMap<String, String>();
         * productionRules2.put("S1", "aSBC");
         * productionRules2.put("BC2", "b");
         * productionRules2.put("C3", "a");
         * productionRules2.put("B4", "b");
         * productionRules2.put("B5", "ε");
         * char[] nonTerminalVariables2 = { 'S', 'B', 'C' };
         * char[] terminalVariables2 = { 'a', 'b' };
         */

        // Grammar grammarTest1 = new Grammar(nonTerminalVariables2, terminalVariables2,
        // productionRules2);
        // System.out.println("The grammar is of " + grammarTest1.classifyGrammar());

        // testing deterministic finite automata

        /*
         * char[] possibleStates2 = { 'S', 'A' };
         * char[] alphabet2 = { 'a', 'b', 'c' };
         * Transition[] transitions2 = new Transition[6];
         * transitions2[0] = new Transition(possibleStates2[0], possibleStates2[1],
         * alphabet2[0]);
         * transitions2[1] = new Transition(possibleStates2[0], possibleStates2[1],
         * alphabet2[1]);
         * transitions2[2] = new Transition(possibleStates2[0], possibleStates2[1],
         * alphabet2[2]);
         * transitions2[3] = new Transition(possibleStates2[1], possibleStates2[0],
         * alphabet2[0]);
         * transitions2[4] = new Transition(possibleStates2[1], possibleStates2[0],
         * alphabet2[1]);
         * transitions2[5] = new Transition(possibleStates2[1], possibleStates2[0],
         * alphabet2[2]);
         * FiniteAutomaton automaton3 = new FiniteAutomaton(possibleStates2, alphabet2,
         * 'S', 'A', transitions2);
         * System.out.println(automaton3.determineType() ?
         * "The finite automata is deterministic"
         * : "The finite automata is non-deterministic");
         */

        char[] possibleStates = { 'S', 'A', 'B', 'C', 'D' };
        char[] alphabet = { 'a', 'b', 'c' };
        Transition[] transitions = new Transition[6];
        transitions[0] = new Transition(possibleStates[0], possibleStates[1], alphabet[0]);
        transitions[1] = new Transition(possibleStates[1], possibleStates[2], alphabet[1]);
        transitions[2] = new Transition(possibleStates[1], possibleStates[3], alphabet[1]);
        transitions[3] = new Transition(possibleStates[2], possibleStates[3], alphabet[2]);
        transitions[4] = new Transition(possibleStates[3], possibleStates[3], alphabet[0]);
        transitions[5] = new Transition(possibleStates[3], possibleStates[4], alphabet[1]);

        FiniteAutomaton automaton2 = new FiniteAutomaton(possibleStates, alphabet, 'S', 'D', transitions);
        Grammar grammar2 = automaton2.toRegularGrammar();

        System.out.println(automaton2.determineType() ? "The finite automata is deterministic"
                : "The finite automata is non-deterministic");
        FiniteAutomaton DFAautomaton = automaton2.toDeterministic();

        System.out.print("Converstion to DFA Results:\nQ = ");

        for (int i = 0; i < DFAautomaton.possibleStatesWithString.length; i++) {
            if (i != DFAautomaton.possibleStatesWithString.length - 1)
                System.out.print(DFAautomaton.possibleStatesWithString[i] + ",");
            else
                System.out.println(DFAautomaton.possibleStatesWithString[i]);
        }

        System.out.print("∑ = ");

        for (int i = 0; i < DFAautomaton.alphabet.length; i++) {
            if (i != DFAautomaton.alphabet.length - 1)
                System.out.print(DFAautomaton.alphabet[i] + ",");
            else
                System.out.println(DFAautomaton.alphabet[i]);
        }

        System.out.println("F = " + DFAautomaton.finalState + "\nTransitions:");

        for (int i = 0; i < DFAautomaton.transitions.length; i++) {
            if (DFAautomaton.transitions[i].currentState2 == null)
                System.out.println(
                        DFAautomaton.transitions[i].currentState + "->" + DFAautomaton.transitions[i].transitionLabel
                                + DFAautomaton.transitions[i].nextState);
            else
                System.out.println(
                        DFAautomaton.transitions[i].currentState2 + "->" + DFAautomaton.transitions[i].transitionLabel
                                + DFAautomaton.transitions[i].nextState2);
        }
        // this is for creating the graphs
        // String automata = automaton2.getTransitionsAsString();
        // String DFAautomata = DFAautomaton.getTransitionsAsString();
        // sendToPython(DFAautomata);
    }

    public static void sendToPython(String automata) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/python3",
                "src/python/graphbuilder.py");
        Process process = pb.start();

        // Get the output and error streams of the Python process
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // Get the input stream of the Python process and write a string to it
        OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
        writer.write(automata + "\n");
        writer.flush();

        // Read and print the output of the Python script
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // Print any errors produced by the Python script
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }

    }
}
