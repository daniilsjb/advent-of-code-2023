import networkx as nx

graph = nx.Graph()

with open('./Day25.txt', 'r') as file:
    for line in file:
        v0, edges = line.split(': ')
        for v1 in edges.split():
            graph.add_edge(v0, v1)
            graph.add_edge(v1, v0)

for edge in nx.minimum_edge_cut(graph):
    graph.remove_edge(*edge)

a, b = list(nx.connected_components(graph))
print(len(a) * len(b))
