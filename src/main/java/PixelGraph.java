import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixelGraph {

    private Map<PixelVertex, List<PixelVertex>> adjVertices;

    public PixelGraph() {
        adjVertices = new HashMap<>();
    }

    public Map<PixelVertex, List<PixelVertex>> getAdjVertices() {
        return new HashMap<>(adjVertices);
    }

    void addVertex(PixelVertex pv) {
        adjVertices.putIfAbsent(pv, new ArrayList<>());
    }

    void removeVertex(PixelVertex pv) {
        // First remove all incoming edges
        adjVertices.forEach((from, to) -> {
            if (to.contains(pv)) {
                removeEdge(from, pv);
            }
        });

        // Now remove all outgoing edges
        for (PixelVertex to : adjVertices.get(pv)) {
            removeEdge(pv, to);
        }

        // Remove the entry from the adjacency table (the List entry for pv should be empty at this point)
        adjVertices.remove(pv);
    }

    void addEdge(PixelVertex from, PixelVertex to) {
        adjVertices.get(from).add(to);
        to.incrInDegree();
    }

    void removeEdge(PixelVertex from, PixelVertex to) {
        List<PixelVertex> edges = adjVertices.get(from);
        if (edges != null) {
            edges.remove(to);
            to.decrInDegree();
        }
    }
}
