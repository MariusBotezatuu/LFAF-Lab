import sys
import graphviz

# Read the string passed from Java on the standard input stream
s = sys.stdin.readline().strip()

# Split the input string into individual transitions
transitions = s.split(";")

# Create a new Graphviz graph
graph = graphviz.Digraph()
graph.attr(rankdir="LR")

# Add each state and transition to the graph
for transition in transitions:
    # Extract the current state, transition label, and next state from the transition string
    current_state, transition_label_next_state = transition.split("->")
    transition_label, next_state = transition_label_next_state[0], transition_label_next_state[1:]

    # Add the current state to the graph (if it hasn't been added already)
    if not graph.node(current_state):
        graph.node(current_state)

    # Add the next state to the graph (if it hasn't been added already)
    if not graph.node(next_state):
        graph.node(next_state, shape="doublecircle" if next_state ==
                   "D" else "circle")

    # Add the transition to the graph
    graph.edge(current_state, next_state, label=transition_label)

# Render the graph
graph.render('finite_automata')
