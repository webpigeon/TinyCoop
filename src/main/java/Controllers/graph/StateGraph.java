package Controllers.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import api.Action;

public class StateGraph {
	private final List<StateAbstraction> vertices;
	private final List<Arc> arcs;
	private final Set<Arc> noops;

	public StateGraph() {
		this.vertices = new ArrayList<>();
		this.arcs = new ArrayList<>();
		this.noops = new HashSet<Arc>();
	}

	public void addTransision(StateAbstraction from, StateAbstraction to, Action action1, Action action2) {
		assert vertices.contains(from);
		assert vertices.contains(to);

		Arc arc = new Arc();
		arc.from = from;
		arc.to = to;
		arc.action1 = action1;
		arc.action2 = action2;
		arc.cost = 1;

		if (from.equals(to)) {
			noops.add(arc);
		}
		arcs.add(arc);
	}

	public void addVertex(StateAbstraction abs) {
		vertices.add(abs);
	}

	public boolean contains(StateAbstraction state) {
		return vertices.contains(state);
	}

	public int getArcCount() {
		return arcs.size();
	}

	public int getArcNoopCount() {
		return noops.size();
	}

	public int getVertexCount() {
		return vertices.size();
	}

}
