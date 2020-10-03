# Voyageur - Functional Graph Algorithms in Java

Relies on [Lamba](https://github.com/palatable/lambda/) for functional paradigms and
[Shoki](https://github.com/palatable/shoki) for immutable data structures.

Inductive, functional graphs, such as those presented by [Martin Erwig](https://web.engr.oregonstate.edu/~erwig/papers/InductiveGraphs_JFP01.pdf).

## Inductive Graphs

The basic interfaces of in Voyageur are (leaving out unification type parameters):
1. `Node<Value>`: nodes containing some sort of `Value` (used to locate them, and treated as a unique index).
   `LabelledNode<Value, Label>` provides a `Label` to attach additional information to a `Node`.
2. `Edge<Value, Node>`: edges from one `Node<Value>` to another. `WeightedEdge<Value, Node, Weight>` can also provide
   different `Weights` to edges (which can also be used to supply metadata).
3. `Context<Value, Node, Edge, Iterable<Edge>>` provides information centered at a `Node`: what edges go into the node
   and which edges go out from it, using potentially any instance of `Iterable` to present the `Edge`s.
4. `Graph<Value, Node, Edge, Iterable<Edge>>` is any structure which can be decomposed into a `Context` centered around
   a node and its edges, and the rest of the `Graph` with no reference to the `Node`. This provides the ability to
   peform various sorts of folds:
   a. `simpleFold` will fold over the entire graph (including disconnected components) and combine the `Node` `Value`s
   using the supplied accumulator function.
   b. `simpleCutFold` will do the same, but will stop when a given `Context` matches a discriminator function.
   c. `guidedFold` takes further parameters to determine how to traverse a graph. While simple folds will
   non-deterministically decompose an inductive graph to traverse it, `guidedFold` uses `State` to choose its nodes.
   d. `guidedCutFold` is the same as `guidedFold`, but has a discrimator function to determine when to stop. Graph
   algorithms in this library are generally implemented using `guidedCutFold`.

## Implementation Types

The main interfaces provide flexibility in applying graph algorithms; anything that meets their requirements can be
treated as a `Node`, `Edge`, and `Graph`. To cut down and type parameters and provide instances of graphs out of the
box, the following implementation types are available:
1. `ValueNode<Value, Label>` is just its value and a label. `node(value)` will produce a `ValueNode<Value, Unit>` if
   labels are not required.
2. `ValueEdge<Value, Label, Weight>` is just the node the edge is from, the node it goes to, and its weight. Like with
   `ValueNode`, static constructor methods are provided which set `Weight` to be `Unit`. All static constructor methods
   make explicit which direction the edge goes in, such as `edgeToFrom` or `edgeFromTo`.
3. `AdjListGraph<Value, Label, Weight>` which provides an adjacency list representation of a graph using
   `ValueNode<Value, Label>` and `ValueEdge<Value, Label, Weight>`.

