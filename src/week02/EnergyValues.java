package week02;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class EnergyValues {
    private static class Equation {
        Equation(double a[][], double b[]) {
            this.a = a;
            this.b = b;
        }

        double a[][];
        double b[];
    }

    private static class Position {
        Position(int column, int raw) {
            this.column = column;
            this.raw = raw;
        }

        int column;
        int raw;
    }

    static Equation ReadEquation() throws IOException {
        Scanner scanner = new Scanner(System.in);
//        Scanner scanner = new Scanner(new FileInputStream("files/week02.EnergyValues.txt"));
        int size = scanner.nextInt();

        double a[][] = new double[size][size];
        double b[] = new double[size];
        for (int raw = 0; raw < size; ++raw) {
            for (int column = 0; column < size; ++column) {
                a[raw][column] = scanner.nextInt();
            }
            b[raw] = scanner.nextInt();
        }
        return new Equation(a, b);
    }

    static Position SelectPivotElement(double a[][], boolean used_raws[], boolean used_columns[]) {
        for (int col = 0; col < used_columns.length; col++) {
            if (!used_columns[col]) {
                for (int row = 0; row < used_raws.length; row++) {
                    if (!used_raws[row] && a[row][col] != 0){
                        return new Position(col, row);
                    }
                }
            }
        }

        return new Position(0, 0);
    }

    static void SwapLines(double a[][], double b[], boolean used_raws[], Position pivot_element) {
        int size = a.length;

        for (int column = 0; column < size; ++column) {
            double tmpa = a[pivot_element.column][column];
            a[pivot_element.column][column] = a[pivot_element.raw][column];
            a[pivot_element.raw][column] = tmpa;
        }

        double tmpb = b[pivot_element.column];
        b[pivot_element.column] = b[pivot_element.raw];
        b[pivot_element.raw] = tmpb;

        boolean tmpu = used_raws[pivot_element.column];
        used_raws[pivot_element.column] = used_raws[pivot_element.raw];
        used_raws[pivot_element.raw] = tmpu;

        pivot_element.raw = pivot_element.column;
    }

    static void ProcessPivotElement(double a[][], double b[], Position pivot_element) {
        // Write your code here
        double divisor = a[pivot_element.raw][pivot_element.column];
        for (int col = pivot_element.column; col < a[pivot_element.raw].length; col++) {
            a[pivot_element.raw][col] /= divisor;
        }
        b[pivot_element.raw] /= divisor;

        for (int row = 0; row < a.length; row++) {
            if (pivot_element.raw != row && a[row][pivot_element.column] != 0){
                double d = a[row][pivot_element.column];
                for (int col = 0; col < a[row].length; col++) {
                    a[row][col] -= d * a[pivot_element.raw][col];
                }
                b[row] -= d * b[pivot_element.raw];
            }
        }
    }

    static void MarkPivotElementUsed(Position pivot_element, boolean used_raws[], boolean used_columns[]) {
        used_raws[pivot_element.raw] = true;
        used_columns[pivot_element.column] = true;
    }

    static double[] SolveEquation(Equation equation) {
        double a[][] = equation.a;
        double b[] = equation.b;
        int size = a.length;

        boolean[] used_columns = new boolean[size];
        boolean[] used_raws = new boolean[size];
        for (int step = 0; step < size; ++step) {
            Position pivot_element = SelectPivotElement(a, used_raws, used_columns);
            SwapLines(a, b, used_raws, pivot_element);
            ProcessPivotElement(a, b, pivot_element);
            MarkPivotElementUsed(pivot_element, used_raws, used_columns);
        }

        return b;
    }

    static void PrintColumn(double column[]) {
        int size = column.length;
        for (int raw = 0; raw < size; ++raw) {
            System.out.printf("%.20f\n", column[raw]);
        }
    }

    public static void main(String[] args) throws IOException {
        Equation equation = ReadEquation();
        double[] solution = SolveEquation(equation);
        PrintColumn(solution);
    }
}
