package TrustE;

import java.util.Random;
import static TrustE.GlobalValue.*;
public class Utils {
    static Random random = new Random();
    static double PI = Math.PI;

    static double sqrt(double x) {
        return Math.sqrt(x);
    }

    static double sqr(double x) {
        return x * x;
    }

    static double abs(double x) {
        return Math.abs(x);
    }

    static double exp(double x) {
        return Math.exp(x);
    }

    static double normal(double x) {
        // Standard Gaussian distribution
        return exp(-0.5 * sqr(x)) / sqrt(2 * PI);
    }

    static int rand() {
        return random.nextInt(32767);
    }

    static double uniform(double min, double max) {
        // generate a float number which is in [min, max), refer to the Python uniform
        return min + (max - min) * Math.random();
    }

    static double vec_len(double[] a, int vec_size) {
        // calculate the length of the vector
        double res = 0;
        for (int i = 0; i < vec_size; i++) {
            res += sqr(a[i]);
        }
        return sqrt(res);
    }

    static void norm(double[] a, int vec_size) {
        // limit the element a under 1
        double x = vec_len(a, vec_size);
        if (x > 1) {
            for (int i = 0; i < vec_size; i++) {
                a[i] /= x;
            }
        }
    }

    static void norm(double[] a, double[][] A, double lr) {
        // limit the element a under 1
        while (true)
        {
            double x=0;
            for (int ii=0; ii<vector_len; ii++)
            {
                double tmp = 0;
                for (int jj=0; jj<m; jj++)
                    tmp+=A[jj][ii]*a[jj];
                x+=sqr(tmp);
            }
            if (x>1)
            {
                double lambda=1;
                for (int ii=0; ii<vector_len; ii++)
                {
                    double tmp = 0;
                    for (int jj=0; jj<m; jj++)
                        tmp+=A[jj][ii]*a[jj];
                    tmp*=2;
                    for (int jj=0; jj<m; jj++)
                    {
                        A[jj][ii]-=lr*lambda*tmp*a[jj];
                        a[jj] -= lr*lambda*tmp*A[jj][ii];
                    }
                }
            }
            else
                break;
        }
    }

    static int rand_max(int x) {
        // get a random number between (0, x)
        int res = (rand() * rand()) % x;
        while (res < 0) {
            res += x;
        }
        return res;
    }
}
