import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class LBGAlgorithm {
    private final RealMatrix inputVectors;
    private final int codeVectorCount;
    RealMatrix codeVector;

    public LBGAlgorithm(RealMatrix inputVectors, int codeVectorCount) {
        this.inputVectors = inputVectors;
        this.codeVectorCount = codeVectorCount;
        this.codeVector = initializeCodeVectors(inputVectors.getColumnDimension());
    }

    public RealMatrix vectorQuantization() {
        int maxIterations = 100; // Set a maximum number of iterations
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<List<RealMatrix>> codeVectorClusters = assignToCodeVectorClusters();
            RealMatrix newCodeVectors = updateCodebook(codeVectorClusters);

            if (hasConverged(codeVector, newCodeVectors)) {
                break; // Convergence reached
            }

            codeVector = newCodeVectors;
        }

        return quantizeData();
    }

    private RealMatrix initializeCodeVectors(int vectorSize) {
        RealMatrix codeVectors = MatrixUtils.createRealMatrix(codeVectorCount, vectorSize);

        for (int i = 0; i < codeVectorCount; i++) {
            int randomIndex = (int) (Math.random() * inputVectors.getRowDimension());
            codeVectors.setRow(i, inputVectors.getRow(randomIndex));
        }

        return codeVectors;
    }

    private List<List<RealMatrix>> assignToCodeVectorClusters() {
        List<List<RealMatrix>> codeVectorClusters = new ArrayList<>();

        for (int i = 0; i < codeVectorCount; i++) {
            codeVectorClusters.add(new ArrayList<>());
        }

        int numRows = inputVectors.getRowDimension();

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            RealMatrix dataPoint = inputVectors.getRowMatrix(rowIndex);
            int assignedCluster = findClosestCodeVector(dataPoint);
            codeVectorClusters.get(assignedCluster).add(dataPoint);
        }

        return codeVectorClusters ;
    }

    private int findClosestCodeVector(RealMatrix dataPoint) {
        double minDistance = Double.MAX_VALUE;
        int closestCodeVector = -1;

        for (int i = 0; i < codeVectorCount; i++) {
            RealMatrix codeVector = this.codeVector.getRowMatrix(i);
            double distance = calculateEuclideanDistance(dataPoint, codeVector );

            if (distance < minDistance) {
                minDistance = distance;
                closestCodeVector  = i;
            }
        }

        return closestCodeVector ;
    }

    private RealMatrix updateCodebook(List<List<RealMatrix>> clusters) {
        RealMatrix newCodebook  = MatrixUtils.createRealMatrix(codeVectorCount, inputVectors.getColumnDimension());

        for (int i = 0; i < codeVectorCount; i++) {
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
        RealMatrix codeVectorAssignments  = MatrixUtils.createRealMatrix(numRows, 1);

        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            RealMatrix dataPoint = inputVectors.getRowMatrix(rowIndex);
            int assignedCluster = findClosestCodeVector(dataPoint);
            codeVectorAssignments .setEntry(rowIndex, 0, assignedCluster);
        }

        return codeVectorAssignments ;
    }

    private static final double CONVERGENCE_THRESHOLD = 1e-6;

    private boolean hasConverged(RealMatrix oldCodeVectors, RealMatrix newCodeVectors) {
        double sumSquaredDiff = oldCodeVectors.subtract(newCodeVectors).getFrobeniusNorm();
        return sumSquaredDiff < CONVERGENCE_THRESHOLD;
    }
}