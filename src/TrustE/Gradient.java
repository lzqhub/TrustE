package TrustE;

import static TrustE.GlobalValue.*;
import static TrustE.GlobalValue.L1_flag;

import static TrustE.Utils.abs;
import static TrustE.Utils.rand_max;
import static TrustE.Utils.sqr;
import static TrustE.Train.*;
import java.util.*;
import static TrustE.Train.et2type;
public class Gradient {

    static double calc_sum(int e1, int e2, int rel) {
        double sum = 0;
        if (L1_flag) {
            for (int i = 0; i < vector_len; i++) {
                sum += abs(entity_vec[e2][i] - entity_vec[e1][i] - relation_vec[rel][i]);
            }
        } else {
            for (int i = 0; i < vector_len; i++) {
                sum += sqr(entity_vec[e2][i] - entity_vec[e1][i] - relation_vec[rel][i]);
            }
        }
        return sum;
    }
//    static double calc_sum2(int e1, int e2) {
//        double sum = 0;
//        if (L1_flag) {
//            for (int i = 0; i < vector_len; i++) {
//                sum += abs(type_vec[e2][i] - entity_vec[e1][i] );
//            }
//        } else {
//            for (int i = 0; i < vector_len; i++) {
//                sum += sqr(type_vec[e2][i] - entity_vec[e1][i] );
//            }
//        }
//        return sum;
//    }
//    static double calc_sum_h(int e1, int e2, int rel) {
//        double sum = 0;
//        if (L1_flag) {
//            for (int i = 0; i < vector_len; i++) {
//                sum += abs(entity_vec[e2][i] - type_vec[e1][i] - relation_vec[rel][i]);
//            }
//        } else {
//            for (int i = 0; i < vector_len; i++) {
//                sum += sqr(entity_vec[e2][i] - type_vec[e1][i] - relation_vec[rel][i]);
//            }
//        }
//        return sum;
//    }
//    static double calc_sum_t(int e1, int e2, int rel) {
//        double sum = 0;
//        if (L1_flag) {
//            for (int i = 0; i < vector_len; i++) {
//                sum += abs(type_vec[e2][i] - entity_vec[e1][i] - relation_vec[rel][i]);
//            }
//        } else {
//            for (int i = 0; i < vector_len; i++) {
//                sum += sqr(type_vec[e2][i] - entity_vec[e1][i] - relation_vec[rel][i]);
//            }
//        }
//        return sum;
//    }
    static double calc_sum2(int e1, int e2) {
        double [] e1_vec = new double[vector_len];
        for (int ii=0; ii<vector_len; ii++)
        {
            for (int jj=0; jj<m; jj++)
            {
                e1_vec[ii]+=A[jj][ii]*type_vec[e2][jj];
            }
        }
        double sum=0;
        if (L1_flag)
            for (int ii=0; ii<vector_len; ii++)
                sum+=abs(e1_vec[ii]-entity_vec[e1][ii]);
        else
            for (int ii=0; ii<vector_len; ii++)
                sum+=sqr(e1_vec[ii]-entity_vec[e1][ii]);
        return sum;
    }

    static double calc_sum_h(int tp, int e2, int rel) {
        double [] type2headet_vec = new double[vector_len];
        for (int ii=0; ii<vector_len; ii++)
        {
            for (int jj=0; jj<m; jj++)
            {
                type2headet_vec[ii]+=A[jj][ii]*type_vec[tp][jj];
            }
        }
        double sum=0;
        if (L1_flag)
            for (int ii=0; ii<vector_len; ii++)
                sum+=abs(entity_vec[e2][ii]-type2headet_vec[ii]-relation_vec[rel][ii]);
        else
            for (int ii=0; ii<vector_len; ii++)
                sum+=sqr(entity_vec[e2][ii]-type2headet_vec[ii]-relation_vec[rel][ii]);
        return sum;
    }
    static double calc_sum_t(int e1, int tp, int rel) {
        double [] type2tailet_vec = new double[vector_len];
        for (int ii=0; ii<vector_len; ii++)
        {
            for (int jj=0; jj<m; jj++)
            {
                type2tailet_vec[ii]+=A[jj][ii]*type_vec[tp][jj];
            }
        }
        double sum=0;
        if (L1_flag)
            for (int ii=0; ii<vector_len; ii++)
                sum+=abs(type2tailet_vec[ii]-relation_vec[rel][ii]-entity_vec[e1][ii]);
        else
            for (int ii=0; ii<vector_len; ii++)
                sum+=sqr(type2tailet_vec[ii]-relation_vec[rel][ii]-entity_vec[e1][ii]);
        return sum;
    }

    static double train_kb(int head_a, int tail_a, int relation_a, int head_b, int tail_b, int relation_b, double res, double lr) {
        double sum1 = calc_sum(head_a, tail_a, relation_a);
        double sum2 = calc_sum(head_b, tail_b, relation_b);

        if (sum1 + margin1 > sum2) {
            res += margin1 + sum1 - sum2;

            gradient(head_a, tail_a, relation_a, head_b, tail_b, relation_b, lr);
//            System.out.printf("res = %f\n", res);
        }
        return res;
    }

    static double train_kbt(int head_a, int tail_a, int head_b, int tail_b, double res, double lr, int i) {
        double sum1 = calc_sum2(head_a, tail_a );
        double sum2 = calc_sum2(head_b, tail_b );

        if (sum1 + margin2 > sum2) {
            res += margin2 + sum1 - sum2;
            double conf = lameda1*rateconf[i]+lameda2*connectconf[i];
            gradient2(head_a, tail_a, head_b, tail_b, lr, conf);
        }
        if (sum1 + margin2 > sum2){
            rateconf[i] *= 0.98;
        }
        else {
            rateconf[i] += 0.001;
            if (rateconf[i]>1.0){
                rateconf[i] = 1.0;
            }
        }

        int ii = rand_max(type_num);
        while (et2type.get(head_a).contains(ii)){
            ii = rand_max(type_num);
        }

        if(tail2triple.containsKey(head_a)) {
            List list = new ArrayList(tail2triple.get(head_a));
            int rn = (int)( Math.random() * list.size());
            int index = (int)(list.get(rn));
            double s1 = calc_sum_t(fb_h.get(index), tail_a, fb_r.get(index));
            double s2 = calc_sum_t(fb_h.get(index), ii, fb_r.get(index));
            if (s1 + margin1  > s2) {
                connectconf[i] *= 0.98;
            } else {
                connectconf[i] += 0.002;
                if (connectconf[i] > 1.0) {
                    connectconf[i] = 1.0;
                }
            }
        }
        else if (head2triple.containsKey(head_a)){
        List list = new ArrayList(head2triple.get(head_a));
            int rn = (int)(Math.random()*list.size());
            int index = (int)(list.get(rn));
            double s1 = calc_sum_h(tail_a, fb_l.get(index), fb_r.get(index));
            double s2 = calc_sum_h(ii, fb_l.get(index), fb_r.get(index));
            if (s1 + margin1 > s2){
                connectconf[i] *= 0.98;
            }
            else {
                connectconf[i] += 0.002;
                if (connectconf[i]>1.0){
                    connectconf[i] = 1.0;
                }
            }
    }

        return res;
    }

    static void gradient(int head_a, int tail_a, int relation_a, int head_b, int tail_b, int relation_b, double lr) {
        for (int i = 0; i < vector_len; i++) {
            double delta1 = entity_vec[tail_a][i] - entity_vec[head_a][i] - relation_vec[relation_a][i];
            double delta2 = entity_vec[tail_b][i] - entity_vec[head_b][i] - relation_vec[relation_b][i];
            double x;
            if (L1_flag) {
                if (delta1 > 0) {
                    x = 1;
                } else {
                    x = -1;
                }
                relation_vec[relation_a][i] += x * lr * 0.1;
                entity_vec[head_a][i] += x * lr;
                entity_vec[tail_a][i] -= x * lr;

                if (delta2 > 0) {
                    x = 1;
                } else {
                    x = -1;
                }
                relation_vec[relation_b][i] -= x * lr * 0.1;
                entity_vec[head_b][i] -= x * lr;
                entity_vec[tail_b][i] += x * lr;
            } else {

                relation_vec[relation_a][i] += lr * 2 * delta1 * 0.1;
                entity_vec[head_a][i] += lr * 2 * delta1;
                entity_vec[tail_a][i] -= lr * 2 * delta1;

                relation_vec[relation_b][i] -= lr * 2 * delta2 * 0.1;
                entity_vec[head_b][i] -= lr * 2 * delta2;
                entity_vec[tail_b][i] += lr * 2 * delta2;
            }
        }
    }

    static void gradient2(int head_a, int tail_a, int head_b, int tail_b, double lr, double conf) {
//        for (int i = 0; i < vector_len; i++) {
//            double delta1 = type_vec[tail_a][i] - entity_vec[head_a][i] ;
//            double delta2 = type_vec[tail_b][i] - entity_vec[head_b][i] ;
//            double x;
//            if (L1_flag) {
//                if (delta1 > 0) {
//                    x = 1;
//                } else {
//                    x = -1;
//                }
//                type_vec[tail_a][i] -= x * lr * conf;
//                entity_vec[head_a][i] += x * lr * conf * 0.02;
//                if (delta2 > 0) {
//                    x = 1;
//                } else {
//                    x = -1;
//                }
//                type_vec[tail_b][i] += x * lr * conf;
//                entity_vec[head_b][i] -= x * lr * conf * 0.02;
//            } else {
//
//                type_vec[tail_a][i] -= lr * 2 * delta1*conf;
//                type_vec[tail_b][i] += lr * 2 * delta2*conf;
//                entity_vec[head_a][i] += lr*2*delta1*conf*0.2;
//                entity_vec[head_b][i] -= lr*2*delta2*conf*0.2;
//            }
//        }
        for (int ii=0; ii<vector_len; ii++)
        {
            double tmp1 = 0, tmp2 = 0;
            for (int jj=0; jj<m; jj++)
            {
                tmp1+=A[jj][ii]*type_vec[tail_a][jj];
                tmp2+=A[jj][ii]*type_vec[tail_b][jj];
            }
            double x1 = 2*(tmp1-entity_vec[head_a][ii]);
            double x2 = 2*(tmp2-entity_vec[head_b][ii]);
            if (L1_flag)
                if (x1>0)
                    x1=1;
                else
                    x1=-1;
                if (x2>0)
                    x2=1;
                else
                    x2=-1;
            for (int jj=0; jj<m; jj++)
            {
                A[jj][ii]-=lr*x1*type_vec[tail_a][jj]*conf;
                A[jj][ii]+=lr*x2*type_vec[tail_b][jj]*conf;
                type_vec[tail_a][jj]-=lr*x1*A[jj][ii]*conf;
                type_vec[tail_b][jj]+=lr*x2*A[jj][ii]*conf;
            }
            entity_vec[head_a][ii] += lr*x1*conf*0.2;
            entity_vec[head_b][ii] -= lr*x2*conf*0.2;
        }
    }
}
