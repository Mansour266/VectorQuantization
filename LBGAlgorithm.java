import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class LBGAlgorithm {
    private RealMatrix inputVectors;

    // k vectors
    private int codevectorCount;
    RealMatrix codevectors;

    public LBGAlgorithm(RealMatrix inputVectors, int codevectorCount) {
        this.inputVectors = inputVectors;
        this.codevectorCount = codevectorCount;
        this.codevectors = initializeCodevectors();
    }

    public RealMatrix vectorQuantization() {
        int maxIterations = 100; // Set a maximum number of iterations
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<List<RealMatrix>> codevectorClusters = assignToCodevectorClusters();
            RealMatrix newCodevectors = updateCodebook(codevectorClusters);

            if (hasConverged(codevectors, newCodevectors)) {
                break; // Convergence reached
            }

            codevectors = newCodevectors;
        }

        return quantizeData();
    }

    private RealMatrix initializeCodevectors() {
        int numRows = inputVectors.getRowDimension();
        int numCols = inputVectors.getColumnDimension();
        RealMatrix codevectors = MatrixUtils.createRealMatrix(codevectorCount, numCols);

        // Initialize codevectors by selecting k random data points
        for (int i = 0; i < codevectorCount; i++) {
            int randomIndex = (int) (Math.random() * numRows);
            codevectors.setRow(i, inputVectors.getRow(randomIndex));
        }

        return codevectors;
    }

    private List<List<RealMatrix>> assignToCodevectorClusters() {
        List<List<RealMatrix>> codevectorClusters = new ArrayList<>();

        for (int i = 0; i < codevectorCount; i++) {
            codevectorClusters.add(new ArrayList<>());
        }

        int numRows = inputVectors.getRowDimension();

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            RealMatrix dataPoint = inputVectors.getRowMatrix(rowIndex);
            int assignedCluster = findClosestCodevector(dataPoint);
            codevectorClusters.get(assignedCluster).add(dataPoint);
        }

        return codevectorClusters ;
    }

    private int findClosestCodevector(RealMatrix dataPoint) {
        double minDistance = Double.MAX_VALUE;
        int closestCodevector = -1;

        for (int i = 0; i < codevectorCount; i++) {
            RealMatrix codevector = codevectors.getRowMatrix(i);
            double distance = calculateEuclideanDistance(dataPoint, codevector );

            if (distance < minDistance) {
                minDistance = distance;
                closestCodevector  = i;
            }
        }

        return closestCodevector ;
    }

    private RealMatrix updateCodebook(List<List<RealMatrix>> clusters) {
        RealMatrix newCodebook  = MatrixUtils.createRealMatrix(codevectorCount, inputVectors.getColumnDimension());

        for (int i = 0; i < codevectorCount; i++) {
            List<RealMatrix> cluster = clusters.get(i);

            if (!cluster.isEmpty()) {
                int numChannels = cluster.get(0).getColumnDimension();
                RealMatrix sum = MatrixUtils.createRealMatrix(1, numChannels);

                for (RealMatrix dataPoint : cluster) {
                    sum = sum.add(dataPoint);
                }

                RealMatrix mean = sum.scalarMultiply(1.0 / cluster.size());
                newCodebook.setRow(i, mean.getRow(0));
            }
        }

        return newCodebook ;
    }

    private double calculateEuclideanDistance(RealMatrix a, RealMatrix b) {
        RealMatrix diff = a.subtract(b);
        RealMatrix squaredDiff = diff.transpose().multiply(diff);
        return Math.sqrt(squaredDiff.getEntry(0, 0));
    }

    private RealMatrix quantizeData() {
        int numRows = inputVectors.getRowDimension();
        RealMatrix codevectorAssignments  = MatrixUtils.createRealMatrix(numRows, 1);

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            RealMatrix dataPoint = inputVectors.getRowMatrix(rowIndex);
            int assignedCluster = findClosestCodevector(dataPoint);
            codevectorAssignments .setEntry(rowIndex, 0, assignedCluster);
        }

        return codevectorAssignments ;
    }

    private static final double CONVERGENCE_THRESHOLD = 1e-6;

    private boolean hasConverged(RealMatrix oldCodevectors, RealMatrix newCodevectors) {
        double sumSquaredDiff = oldCodevectors.subtract(newCodevectors).getFrobeniusNorm();
        return sumSquaredDiff < CONVERGENCE_THRESHOLD;
    }
}
