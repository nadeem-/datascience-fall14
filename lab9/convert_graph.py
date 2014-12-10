import sys

vertices_fname = "vertices.csv"
edges_fname = "edges.csv"
output_fname = "cypher_graph_script.txt"

out = open(output_fname, 'w')

with open(vertices_fname) as f:
	lines = f.readlines()
	for l in lines:		
		tupls = l.rstrip().split("),")
		for t in tupls:
			t = t.lstrip().rstrip()[1:]
			t = t.split(",")
			if(len(t) == 2):
				state_id,state = t
				state = state.replace('"','').lstrip().replace(")","")
				state_id = "L" + state_id[:-1]
				s = "CREATE (" + state_id + ":Vertex" + " {state:'" + state + "'})"
				print(s)
				out.write(s + "\n")
	f.close


with open(edges_fname) as f:
	lines = f.readlines()
	for l in lines:		
		tupls = l.rstrip().split("),")
		for t in tupls:
			t = t.lstrip().rstrip()[5:]
			t = t.split(",")
			if(len(t) == 3):
				e1,e2,w = t
				e2 = e2.lstrip()
				w = w.lstrip().replace(")","")

				e1 = "L" + e1[:-1]
				e2 = "L" + e2[:-1]

				s = "CREATE (" + e1 + ")-[:BORDERS {weight:" + w + "}]->(" + e2 + ")"
				print(s)
				out.write(s + "\n")
	f.close

out.write("\nRETURN L1\n;\n")
out.close()
# Edge(1L, 9L, 196.1)
# CREATE (1L)-[:BORDERS {weight:196.1}]->(9L)

