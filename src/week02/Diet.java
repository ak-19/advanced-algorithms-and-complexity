package week02;

import java.io.*;
import java.util.*;

public class Diet {
    public static class SubArraysOfArray {
        private int subArraySize;
        private int arraySize;
        private List<int[]> subArrays;
        private int[] aray;

        public SubArraysOfArray(int subArraySize, int arraySize) {
            this.subArraySize = subArraySize;
            this.arraySize = arraySize;
            subArrays = new ArrayList<int[]>();
            if (subArraySize < 1) {
                return;
            }

            this.aray = new int[arraySize];
            for (int i = 0; i < arraySize; i++) {
                aray[i] = i;
            }

            for (int i = 0; i <= arraySize - subArraySize; i++) {
                int[] combination = new int[subArraySize];
                combination[0] = aray[i];
                setSingleItem(i + 1, combination, 1);
            }
        }

        private void setSingleItem(int pos, int[] combination, int height) {
            if (height >= this.subArraySize) {
                subArrays.add(combination);
                return;
            }
            for (int p = pos; p <= arraySize - subArraySize + height; p++) {
                int[] newCombination = combination.clone();
                newCombination[height] = aray[p];
                setSingleItem(p + 1, newCombination, height + 1);
            }
        }

        public List<int[]> getSubArrays() {
            return subArrays;
        }
    }

    private int[] notUsedIndices(int arraySize, int[] indices) {
        if (indices.length >= arraySize) {
            return new int[0];
        }


        int[] a = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            a[i] = i;
        }

        int[] other = new int[a.length - indices.length];
        Set<Integer> used = new HashSet<Integer>();
        for (Integer u : indices) {
            used.add(u);
        }
        int counter = 0;
        for (int i = 0; i < a.length; i++) {
            if (!used.contains(a[i])) {
                other[counter++] = a[i];
            }
        }
        return other;
    }

    private void constructCurrentForm(double[][] constraints, double[] budgets, double[][] currForm, double[] currFormBudget, int[] indexes) {
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] < constraints.length) {
                currForm[i] = Arrays.copyOf(constraints[indexes[i]], constraints[i].length);
                currFormBudget[i] = budgets[indexes[i]];
            } else if (indexes[i] == constraints.length + constraints[0].length) {
                Arrays.fill(currForm[i], 1);
                currFormBudget[i] = 1000000000d;
            } else {
                currForm[i][indexes[i] - constraints.length] = -1;
            }
        }
    }

    private int solveDietProblem(int constraintsNumber, int unknownsNumber, double constraints[][], double[] budgets, double[] pleasures, double[] x) {
        Double maxValue = Double.NaN;
        double[] unknownsSolution = new double[unknownsNumber];
        List<int[]> combinations = new SubArraysOfArray(unknownsNumber, constraintsNumber + unknownsNumber + 1).getSubArrays();
        for (int[] combination : combinations) {
            double[][] currForm = new double[unknownsNumber][unknownsNumber];
            double[] currFormBudget = new double[unknownsNumber];
            constructCurrentForm(constraints, budgets, currForm, currFormBudget, combination);
            double[] solutionCandidate;
            try {
                solutionCandidate = rowReduce(currForm, currFormBudget);
            } catch (Exception e) {
                continue;
            }

            int[] nonUsedIndices = notUsedIndices(constraintsNumber + unknownsNumber + 1, combination);
            currForm = new double[nonUsedIndices.length][unknownsNumber];
            currFormBudget = new double[nonUsedIndices.length];
            constructCurrentForm(constraints, budgets, currForm, currFormBudget, nonUsedIndices);
            if (isSolutionVerifiedAgainstAllConstraints(solutionCandidate, currForm, currFormBudget)) {
                double maxValueCandidate = calculateMaxValueCandidate(solutionCandidate, pleasures);
                if (maxValue.isNaN() || maxValueCandidate > maxValue) {
                    maxValue = maxValueCandidate;
                    unknownsSolution = Arrays.copyOf(solutionCandidate, solutionCandidate.length);
                }
            }

        }

        int NoSolution = -1;
        int BoundedSolution = 0;
        int UnboundedSolution = 1;

        if (maxValue.isNaN()) {
            return NoSolution;
        }
        if (maxValue > 999999990d) {
            return UnboundedSolution;
        }

        for (int i = 0; i < unknownsNumber; i++) {
            x[i] = unknownsSolution[i];
        }

        return BoundedSolution;
    }

    private double calculateMaxValueCandidate(double[] solutionCandidate, double[] pleasures) {
        double maxValue = 0;
        for (int unknown = 0; unknown < solutionCandidate.length; unknown++) {
            maxValue += solutionCandidate[unknown] * pleasures[unknown];
        }

        return maxValue;
    }

    private boolean isSolutionVerifiedAgainstAllConstraints(double[] solution, double[][] constraints, double[] budgets) {
        for (int row = 0; row < constraints.length; row++) {
            double LHS = 0;
            double RHS = budgets[row];

            for (int unknown = 0; unknown < solution.length; unknown++) {
                LHS += solution[unknown] * constraints[row][unknown];
            }

            if (LHS > RHS) {
                return false;
            }
        }

        return true;
    }

    public static double[] rowReduce(double A[][], double[] b) {
        int N = A[0].length;
        for (int p = 0; p < N; p++) {
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            if (Math.abs(A[max][p]) <= 0.000001) {
                throw new IllegalArgumentException();
            }
            double[] temp = A[p];
            A[p] = A[max];
            A[max] = temp;
            double t = b[p];
            b[p] = b[max];
            b[max] = t;

            for (int i = p + 1; i < N; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < N; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        double[] solution = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += A[i][j] * solution[j];
            }
            solution[i] = (b[i] - sum) / A[i][i];
        }

        return solution;
    }

    private void solve() throws IOException {
        int n = nextInt();
        int m = nextInt();
        double[][] A = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                A[i][j] = nextInt();
            }
        }
        double[] b = new double[n];
        for (int i = 0; i < n; i++) {
            b[i] = nextInt();
        }
        double[] c = new double[m];
        for (int i = 0; i < m; i++) {
            c[i] = nextInt();
        }
        double[] ansx = new double[m];
        int anst = solveDietProblem(n, m, A, b, c, ansx);
        if (anst == -1) {
            out.printf("No solution\n");
            return;
        }
        if (anst == 0) {
            out.printf("Bounded solution\n");
            for (int i = 0; i < m; i++) {
                out.printf("%.18f%c", ansx[i], i + 1 == m ? '\n' : ' ');
            }
            return;
        }
        if (anst == 1) {
            out.printf("Infinity\n");
            return;
        }
    }

    public static void main(String[] args) throws IOException {
        new Diet();
    }

    Diet() throws IOException {
          br = new BufferedReader(new InputStreamReader(System.in));
        // br = new BufferedReader(new InputStreamReader(new FileInputStream("files/Diet.txt")));
        out = new PrintWriter(System.out);
        solve();
        out.close();
    }

    private BufferedReader br;
    private PrintWriter out;
    private StringTokenizer st;
    private boolean eof;

    private String nextToken() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (Exception e) {
                eof = true;
                return null;
            }
        }
        return st.nextToken();
    }

    private int nextInt() throws IOException {
        return Integer.parseInt(nextToken());
    }
}
