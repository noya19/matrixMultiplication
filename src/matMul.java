import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class matMul {

  private static ThreadPoolExecutor executor;

  static {
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);
  }

  private static class Job implements Runnable {

    private int[][] matA, matB, output;
    private int i, j;

    public Job(int[][] matA, int[][] matB, int[][] output, int i, int j) {
      this.matA = matA;
      this.matB = matB;
      this.output = output;
      this.i = i;
      this.j = j;
    }

    public void run() {
      int sum = 0;
      for (int k = 0; k < matB.length; k++) {
        sum += matA[i][k] * matB[k][j];
      }
      output[i][j] = sum;
    }
  }

  public static int[][] matMulParallel(int[][] matA, int[][] matB) {
    int rMatA = matA.length;
    int cMatA = matA[0].length;
    int rMatB = matB.length;
    int cMatB = matB[0].length;

    if (cMatA != rMatB) return new int[0][0];

    int[][] ouput = new int[rMatA][cMatB];

    for (int i = 0; i < rMatA; i++) {
      for (int j = 0; j < cMatB; j++) {
        executor.submit(new Job(matA, matB, ouput, i, j));
      }
    }

    return ouput;
  }

  public static int[][] generateMatrix(int rows, int cols) {
    int[][] randomMatrix = new int[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        randomMatrix[i][j] = (int) Math.ceil(Math.random() * 100);
      }
    }

    return randomMatrix;
  }

  public static int[][] matMulNaive(int[][] matA, int[][] matB) {
    int rMatA = matA.length;
    int cMatA = matA[0].length;
    int rMatB = matB.length;
    int cMatB = matB[0].length;

    if (cMatA != rMatB) return new int[0][0];

    int[][] ouput = new int[rMatA][cMatB];

    for (int i = 0; i < rMatA; i++) {
      for (int j = 0; j < cMatB; j++) {
        int cellValue = 0;
        for (int k = 0; k < cMatA; k++) {
          cellValue += matA[i][k] * matB[k][j];
        }
        ouput[i][j] = cellValue;
      }
    }

    return ouput;
  }

  public static void main(String[] args) {
    int[][] matA = generateMatrix(1000, 1500);
    int[][] matB = generateMatrix(1500, 1105);

    long t = System.currentTimeMillis();
    int[][] output = matMulParallel(matA, matB);
    long n = System.currentTimeMillis();
    System.out.printf("Parallel Multiplication: %d millisecs\n", n - t);

    t = n;
    int[][] arr2 = matMulNaive(matA, matB);
    n = System.currentTimeMillis();
    System.out.printf("Naive Multiplication: %d millisecs\n", n - t);

    boolean matches = true;
    for (int i = 0; i < output.length; i++) {
      for (int j = 0; j < output[i].length; j++) {
        if (output[i][j] != arr2[i][j]) matches = false;
      }
    }

    if (matches) {
      System.out.println("Answer matches");
    } else {
      System.out.println("Answer does not match");
    }

    executor.shutdown();
  }
}
