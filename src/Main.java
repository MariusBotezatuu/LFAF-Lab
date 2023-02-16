import java.util.HashMap;

import automaton.FiniteAutomaton;
import grammar.Grammar;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

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
        System.out.println("5 strings by the given grammar: " + wordExamples);

        FiniteAutomaton automaton = grammar.toFiniteAutomaton();
        String wordToCheck = "deeeeeed";
        System.out.println(automaton.stringBelongToLanguage(wordToCheck)
                ? ("The string \"" + wordToCheck + "\" belongs to the language")
                : ("The string \"" + wordToCheck + "\" does not belong to the language"));
    }
}
