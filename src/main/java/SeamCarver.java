import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class SeamCarver {

    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    public Picture picture() {
        return new Picture(this.picture);
    }

    public int width() {
        return this.picture.width();
    }

    public int height() {
        return this.picture.height();
    }

    public double energy(int x, int y) {
        validateCoords(x, y);
        return Math.pow(xGradient(x, y), 2) + Math.pow(yGradient(x, y), 2);
    }

    private void validateCoords(int x, int y) {
        if (!(x >= 0 && x < width())) {
            throw new IndexOutOfBoundsException(x + " is not a valid x value");
        }
        if (!(y >= 0 && y < height())) {
            throw new IndexOutOfBoundsException(y + " is not a valid y value");
        }
    }

    private double xGradient(int x, int y) {
        // Use x-adjacent pixels for gradient calculation
        // If pixel is on border use pixel from opposite side of image
        int leftX = (x == 0) ? width() - 1 : x - 1;
        int rightX = (x == width() - 1) ? 0 : x + 1;

        Color leftPixel = picture.get(leftX, y);
        Color rightPixel = picture.get(rightX, y);

        return gradient(leftPixel, rightPixel);
    }

    private double yGradient(int x, int y) {
        // Use x-adjacent pixels for gradient calculation
        // If pixel is on border use pixel from opposite side of image
        int topY = (y == 0) ? height() - 1 : y - 1;
        int bottomY = (y == height() - 1) ? 0 : y + 1;

        Color topPixel = picture.get(x, topY);
        Color bottomPixel = picture.get(x, bottomY);

        return gradient(topPixel, bottomPixel);
    }

    private double gradient(Color p1, Color p2) {
        return (
                Math.pow(p1.getRed() - p2.getRed(), 2) +
                Math.pow(p1.getGreen() - p2.getGreen(), 2) +
                Math.pow(p1.getBlue() - p2.getBlue(), 2)
        );
    }

    public int[] findHorizontalSeam() {
        // TODO
        return new int[0];
    }

    private PixelGraph horizontalPixelGraph() {
        // Add vertices to graph
        PixelGraph graph = loadPixelVertices();

        // Add all connecting edges
        graph.getAdjVertices().forEach((k, v) -> {
            int x = k.getX(), y = k.getY();
            if (x < width() - 1) {
                graph.addEdge(k, new PixelVertex(x+1, y, energy(x, y+1)));
                if (y > 0) {
                    graph.addEdge(k, new PixelVertex(x+1, y-1, energy(x-1, y)));
                }
                if (y < height() - 1) {
                    graph.addEdge(k, new PixelVertex(x+1, y+1, energy(x+1, y)));
                }
            }
        });

        return graph;
    }

    public int[] findVerticalSeam() {
        // Topological sort the pixel graph
        // Generate vertical pixel graph
        PixelGraph graph = verticalPixelGraph();
        System.out.println("Graph creation complete.");

        // Topologically sort pixel graph
        PixelVertex[] topSort = verticalTopSort();
        System.out.println("Topological sort complete.");

        // Find shortest path given topological sort
        // Create array for shortest path from each of the possible sources (the entire top row)
        ShortestPathReturn[] sourcePaths = new ShortestPathReturn[width()];
        for (int source = 0; source < width(); source++) {
            sourcePaths[source] = singleSourceShortestPath(topSort, graph, source);
            System.out.println("Source " + source + " evaluated.");
        }
        System.out.println("Path array created.");

        // Find minimum path size by looping through last width() of each of the distances
        double minimumPathSize = Double.MAX_VALUE;
        int minimumPathSourceIndex = -1;
        int minimumPathFinalIndex = -1;  // Index in the topSort, not the row index
        for (int sourceIndex = 0; sourceIndex < sourcePaths.length; sourceIndex++) {
            for (int finalIndex = topSort.length - width(); finalIndex < topSort.length; finalIndex++) {
                // Total distance has to take into account energy of source pixel
                double distance = sourcePaths[sourceIndex].distances[finalIndex] + energy(sourceIndex, 0);
                if (distance < minimumPathSize) {
                    minimumPathSize = distance;
                    minimumPathSourceIndex = sourceIndex;
                    minimumPathFinalIndex = finalIndex;
                }
            }
        }
        System.out.println("Minimum path size: " + minimumPathSize);

        // Loop through the predecessor array to construct the final verticalSeam array, where each seam[i] contains
        // the index (x value) of the pixel to remove in the ith row
        Integer[] minimumPathPredecessorArray = sourcePaths[minimumPathSourceIndex].predecessors;
        int[] verticalSeam = new int[height()];
        int predecessor = minimumPathFinalIndex;
        for (int i = verticalSeam.length - 1; i <= 0; i++) {
            verticalSeam[i] = topSort[predecessor].getX();
            predecessor = minimumPathPredecessorArray[predecessor];
        }
        System.out.println("Vertical seam constructed.");

        return verticalSeam;
    }

    private PixelGraph loadPixelVertices() {
        PixelGraph graph = new PixelGraph();
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                PixelVertex pv = new PixelVertex(x, y, energy(x, y));
                graph.addVertex(pv);
            }
        }

        return graph;
    }

    private ShortestPathReturn singleSourceShortestPath(PixelVertex[] topSort, PixelGraph graph, int sourceIndex) {
        // Create array to store distances from source to destination and initialize distances to
        // 0 for source and infinity (Double.MAX_VALUE) for all following nodes
        double[] distances = new double[topSort.length];
        Arrays.fill(distances, Double.MAX_VALUE);
        distances[sourceIndex] = 0;

        // Create array to store INDEX OF predecessor in shortest path, and use to create int[] of indexes of
        // path later; use Integer instead of int to initialize with null values
        Integer[] predecessors = new Integer[topSort.length];
        Arrays.fill(predecessors, null);

        System.out.println("Distances and predecessors array created.");

        for (int u = sourceIndex; u < topSort.length; u++) {
            System.out.println("singleSourceShortestPath at vertex " + u);
            PixelVertex pvu = topSort[u];
            List<PixelVertex> adjVertices = graph.getAdjVertices().get(pvu);
            System.out.println(adjVertices);
            for (int v = 0; v < adjVertices.size(); v++) {
                PixelVertex followingVertex = adjVertices.get(v);
                // Treat edge weight as energy of following pixel (cost to move to the following pixel)
                double edgeWeight = followingVertex.getWeight();
                if (distances[u] + edgeWeight < distances[v]) {
                    // Relax the edge since the path from the source to pv to the following vertex
                    // is shorter than the currently stored shortest path
                    System.out.println("Edge relaxed.");
                    distances[v] = distances[u] + edgeWeight;
                    predecessors[v] = u;
                }
            }
        }

        return new ShortestPathReturn(distances, predecessors);
    }

    class ShortestPathReturn {
        double[] distances;
        Integer[] predecessors;

        ShortestPathReturn(double[] distances, Integer[] predecessors) {
            this.distances = distances;
            this.predecessors = predecessors;
        }
    }

    private PixelGraph verticalPixelGraph() {
        // Add vertices to graph
        PixelGraph graph = loadPixelVertices();

        // Add all connecting edges
        graph.getAdjVertices().forEach((k, v) -> {
            int x = k.getX(), y = k.getY();
            if (y < height() - 1) {
                graph.addEdge(k, new PixelVertex(x, y+1, energy(x, y+1)));
                if (x > 0) {
                    graph.addEdge(k, new PixelVertex(x-1, y+1, energy(x-1, y+1)));
                }
                if (x < width() - 1) {
                    graph.addEdge(k, new PixelVertex(x+1, y+1, energy(x+1, y+1)));
                }
            }
        });

        return graph;
    }

    private PixelVertex[] verticalTopSort() {
        // Since each pixel can only point to pixels below itself, arranging the pixels row by row essentially
        // creates a topological sort of the pixel graph
        PixelVertex[] topSort = new PixelVertex[width() * height()];
        int x = 0;
        int y = 0;
        for (int i = 0; i < topSort.length; i++) {
            topSort[i] = new PixelVertex(x, y, energy(x, y));

            x++;
            if (x >= width()) {
                y++;
                x = 0;
            }
        }

        return topSort;
    }

    public void removeHorizontalSeam(int[] seam) {
        // TODO
    }

    public void removeVerticalSeam(int[] seam) {
        // TODO
    }




}
