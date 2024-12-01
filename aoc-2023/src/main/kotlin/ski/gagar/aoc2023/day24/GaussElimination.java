package ski.gagar.aoc2023.day24;

/**
 ** Java Program to Implement Gaussian Elimination Algorithm
 **/

import java.util.Scanner;

/** Class GaussianElimination **/
public class GaussElimination
{
    public void solve(double[][] A, double[] B)
    {
        int N = B.length;
        for (int k = 0; k < N; k++)
        {
            /** find pivot row **/
            int max = k;
            for (int i = k + 1; i < N; i++)
                if (Math.abs(A[i][k]) > Math.abs(A[max][k]))
                    max = i;

            /** swap row in A matrix **/
            double[] temp = A[k];
            A[k] = A[max];
            A[max] = temp;

            /** swap corresponding values in constants matrix **/
            double t = B[k];
            B[k] = B[max];
            B[max] = t;

            /** pivot within A and B **/
            for (int i = k + 1; i < N; i++)
            {
                double factor = A[i][k] / A[k][k];
                B[i] -= factor * B[k];
                for (int j = k; j < N; j++)
                    A[i][j] -= factor * A[k][j];
            }
        }

        /** Print row echelon form **/
        printRowEchelonForm(A, B);

        /** back substitution **/
        double[] solution = new double[N];
        for (int i = N - 1; i >= 0; i--)
        {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++)
                sum += A[i][j] * solution[j];
            solution[i] = (B[i] - sum) / A[i][i];
        }
        /** Print solution **/
        printSolution(solution);
    }
    /** function to print in row    echleon form **/
    public void printRowEchelonForm(double[][] A, double[] B)
    {
        int N = B.length;
        System.out.println("\nRow Echelon form : ");
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
                System.out.printf("%.3f ", A[i][j]);
            System.out.printf("| %.3f\n", B[i]);
        }
        System.out.println();
    }
    /** function to print solution **/
    public void printSolution(double[] sol)
    {
        int N = sol.length;
        System.out.println("\nSolution : ");
        for (int i = 0; i < N; i++)
            System.out.printf("%.3f ", sol[i]);
        System.out.println();
    }
    /** Main function **/
    public static void main (String[] args)
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Gaussian Elimination Algorithm Test\n");
        /** Make an object of GaussianElimination class **/
        GaussElimination ge = new GaussElimination();

        double[] B = {-45,56,22,-10,32,-12,-7,-25,16,119,-18,-75};
        double[][] A = {
                {-1,13,-2,-19,0,0,1,-1,0,0,0,0},
                {0,0,2,30,1,-13,0,0,1,-1,0,0},
                {-2,-30,0,0,2,19,0,0,0,0,1,-1},
                {2,25,-2,-20,0,0,1,-1,0,0,0,0},
                {0,0,4,34,-2,-25,0,0,1,-1,0,0},
                {-4,-34,0,0,2,20,0,0,0,0,1,-1},
                {2,31,-1,-12,0,0,1,-1,0,0,0,0},
                {0,0,1,28,-2,-31,0,0,1,-1,0,0},
                {-1,-28,0,0,1,12,0,0,0,0,1,-1},
                {5,19,1,-20,0,0,1,-1,0,0,0,0},
                {0,0,3,15,-5,-19,0,0,1,-1,0,0},
                {-3,-15,0,0,-1,20,0,0,0,0,1,-1}
        };


        ge.solve(A,B);
    }
}