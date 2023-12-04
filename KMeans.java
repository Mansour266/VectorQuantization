import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class KMeans {
    private RealMatrix dataMatrix;
    private int k;
    private RealMatrix centroids;

    public KMeans(RealMatrix dataMatrix, int k) {
        this.dataMatrix = dataMatrix;
        this.k = k;
        this.centroids = initializeCentroids();
    }

    public RealMatrix cluster() {
        int maxIterations = 100; // Set a maximum number of iterations
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<List<RealMatrix>> clusters = assignToClusters();
            RealMatrix newCentroids = calculateCentroids(clusters);

            if (hasConverged(centroids, newCentroids)) {
                break; // Convergence reached
            }

            centroids = newCentroids;
        }

        return assignToClustersMatrix();
    }

    private RealMatrix initializeCentroids() {
        int numRows = dataMatrix.getRowDimension();
        int numCols = dataMatrix.getColumnDimension();
        RealMatrix centroids = MatrixUtils.createRealMatrix(k, numCols);

        // Initialize centroids by selecting k random data points
        for (int i = 0; i < k; i++) {
            int randomIndex = (int) (Math.random() * numRows);
            centroids.setRow(i, dataMatrix.getRow(randomIndex));
        }

        return centroids;
    }

    private List<List<RealMatrix>> assignToClusters() {
        List<List<RealMatrix>> clusters = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            clusters.add(new ArrayList<>());
        }

        int numRows = dataMatrix.getRowDimension();

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            RealMatrix dataPoint = dataMatrix.getRowMatrix(rowIndex);
            int assignedCluster = findClosestCentroid(dataPoint);
            clusters.get(assignedCluster).add(dataPoint);
        }

        return clusters;
    }

    private int findClosestCentroid(RealMatrix dataPoint) {
        double minDistance = Double.MAX_VALUE;
        int closestCentroid = -1;

        for (int i = 0; i < k; i++) {
            RealMatrix centroid = centroids.getRowMatrix(i);
            double distance = calculateEuclideanDistance(dataPoint, centroid);

            if (distance < minDistance) {
                minDistance = distance;
                closestCentroid = i;
            }
        }

        return closestCentroid;
    }

    private RealMatrix calculateCentroids(List<List<RealMatrix>> clusters) {
        RealMatrix newCentroids = MatrixUtils.createRealMatrix(k, dataMatrix.getColumnDimension());

        for (int i = 0; i < k; i++) {
            List<RealMatrix> cluster = clusters.get(i);

            if (!cluster.isEmpty()) {
                RealMatrix sum = cluster.stream().reduce(MatrixUtils.createRealMatrix(dataMatrix.getColumnDimension(), 1), RealMatrix::add);
                RealMatrix mean = sum.scalarMultiply(1.0 / cluster.size());
                newCentroids.setRow(i, mean.getColumn(0));
            }
        }

        return newCentroids;
    }

    private double calculateEuclideanDistance(RealMatrix a, RealMatrix b) {
        RealMatrix diff = a.subtract(b);
        RealMatrix squaredDiff = diff.transpose().multiply(diff);
        return Math.sqrt(squaredDiff.getEntry(0, 0));
    }

    private RealMatrix assignToClustersMatrix() {
        int numRows = dataMatrix.getRowDimension();
        RealMatrix clusterAssignments = MatrixUtils.createRealMatrix(numRows, 1);

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            RealMatrix dataPoint = dataMatrix.getRowMatrix(rowIndex);
            int assignedCluster = findClosestCentroid(dataPoint);
            clusterAssignments.setEntry(rowIndex, 0, assignedCluster);
        }

        return clusterAssignments;
    }

    private static final double CONVERGENCE_THRESHOLD = 1e-6;

    private boolean hasConverged(RealMatrix oldCentroids, RealMatrix newCentroids) {
        double sumSquaredDiff = oldCentroids.subtract(newCentroids).getFrobeniusNorm();
        return sumSquaredDiff < CONVERGENCE_THRESHOLD;
    }
}
