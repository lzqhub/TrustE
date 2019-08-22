package TrustE;

import java.io.*;
import java.util.*;
import static TrustE.GlobalValue.learning_rate1;
import static TrustE.GlobalValue.learning_rate2;

import static TrustE.Gradient.train_kb;
import  static TrustE.Gradient.train_kbt;
import static TrustE.Utils.*;
import static TrustE.GlobalValue.*;

public class Train {

    private double res; //loss function value
    private double res2;
    static List<Integer> fb_h;
    static List<Integer> fb_l;
    static List<Integer> fb_r;
    private List<Integer> fb_ht;
    private List<Integer> fb_lt;
    private Map<Pair<Integer, Integer>, Set<Integer>> head_relation2tail; // to save the (h, r, t)

    static Map<Integer, Set<Integer>> head2triple;
    static Map<Integer, Set<Integer>> tail2triple;
    static Map<Integer, Set<Integer>> et2type;
    Train() {
        fb_h = new ArrayList<>();
        fb_l = new ArrayList<>();
        fb_r = new ArrayList<>();
        fb_ht = new ArrayList<>();
        fb_lt = new ArrayList<>();
        head_relation2tail = new HashMap<>();
        et2type = new HashMap<>();
        head2triple = new HashMap<>();
        tail2triple = new HashMap<>();
    }

    private void Write_Vec2File(String file_name, double[][] vec, int number, int veclen) throws IOException {
        File f = new File(file_name);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
        for (int i = 0; i < number; i++) {
            for (int j = 0; j < veclen; j++) {
                String str = String.format("%.6f\t", vec[i][j]);
                writer.write(str);
            }
            writer.write("\n");
            writer.flush();
        }
    }



    private void Write_Vec2File(String file_name, double[] vec, int num) throws IOException {
        File f = new File(file_name);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < 1; j++) {
                String str = String.format("%.6f", vec[i]);
                writer.write(str);
            }
            writer.write("\n");
            writer.flush();
        }
    }

    private void bfgs(int nepoch, int nbatches) throws IOException {
        int batchsize = fb_h.size() / nbatches;
        System.out.printf("Batch size = %s\n", batchsize);
        for (int epoch = 0; epoch < nepoch; epoch++) {
            res = 0;  // means the total loss in each epoch
            res2 = 0;
            double lr1,lr2 = 0;
            if(epoch<50){
                lr1 = learning_rate1;
                lr2 = learning_rate2;
            }
            else if (epoch<800){
                lr1 = 50*learning_rate1/epoch;
                lr2 = 50*learning_rate2/epoch;
            }
            else {
                lr1 = 0.0001;
                lr2 = 0.0001;
            }
            for (int batch = 0; batch < nbatches; batch++) {
                for (int k = 0; k < batchsize; k++) {
                    int i = rand_max(fb_h.size());
                    int j = rand_max(entity_num);
                    int relation_id = fb_r.get(i);

                    int ii = rand_max(fb_ht.size());
                    int jj = rand_max(type_num);

                    int kk = rand_max(entity_num);
                    double pr = 1000 * right_num.get(relation_id) / (right_num.get(relation_id) + left_num.get(relation_id));
                    if (method == 0) {
                        pr = 500;
                    }
                    if (rand() % 1000 < pr) {
                        Pair<Integer, Integer> key = new Pair<>(fb_h.get(i), fb_r.get(i));
                        Set<Integer> values = head_relation2tail.get(key);  // 获取头实体和关系对应的尾实体集合
                        while (values.contains(j)) {
                            j = rand_max(entity_num);
                        }
                        while(et2type.get(fb_ht.get(ii)).contains(jj)){
                            jj = rand_max(type_num);
                        }
                        res = train_kb(fb_h.get(i), fb_l.get(i), fb_r.get(i), fb_h.get(i), j, fb_r.get(i), res, lr1);
                        res2 = train_kbt(fb_ht.get(ii), fb_lt.get(ii), fb_ht.get(ii), jj, res2, lr2, ii);
                    } else {
                        Pair<Integer, Integer> key = new Pair<>(j, fb_r.get(i));
                        Set<Integer> values = head_relation2tail.get(key);
                        if (values != null) {
                            while (values.contains(fb_l.get(i))) {
                                j = rand_max(entity_num);
                                key = new Pair<>(j, fb_r.get(i));
                                values = head_relation2tail.get(key);
                                if (values == null) break;
                            }
                        }
                        while(!et2type.containsKey(kk)||et2type.get(kk).contains(fb_lt.get(ii))){
                            kk = rand_max(entity_num);
                        }
                        res = train_kb(fb_h.get(i), fb_l.get(i), fb_r.get(i), j, fb_l.get(i), fb_r.get(i), res, lr1);
                        res2 = train_kbt(fb_ht.get(ii), fb_lt.get(ii), kk, fb_lt.get(ii),  res2, lr2, ii);
                    }
                    norm(relation_vec[fb_r.get(i)], vector_len);
                    norm(entity_vec[fb_h.get(i)], vector_len);
                    norm(entity_vec[fb_l.get(i)], vector_len);
                    norm(entity_vec[j], vector_len);
                    norm(type_vec[fb_lt.get(ii)], vector_len);
                    norm(type_vec[jj], vector_len);
                    norm(type_vec[fb_lt.get(ii)], A,lr1);
                    norm(type_vec[jj], A,lr1);
                    norm(entity_vec[kk],vector_len);
                    norm(entity_vec[fb_ht.get(ii)],vector_len);
                }
            }
            System.out.printf("epoch: %s %s\n", epoch, res);
            System.out.printf("epoch: %s %s\n", epoch, res2);
        }
        Write_Vec2File("TrustE/result/relation2vec." + version, relation_vec, relation_num, vector_len);
        Write_Vec2File("TrustE/result/entity2vec." + version, entity_vec, entity_num, vector_len);
        Write_Vec2File("TrustE/result/type2vec." + version, type_vec, type_num, vector_len);
        Write_Vec2File("TrustE/result/A." + version, A, m, vector_len);
        Write_Vec2File("TrustE/result/rateconf." + version, rateconf, fb_ht.size());
        Write_Vec2File("TrustE/result/connectconf." + version, connectconf, fb_ht.size());
    }

    // region public members & methods

    public void add(int head, int relation, int tail, int i) {
        fb_h.add(head);
        fb_r.add(relation);
        fb_l.add(tail);

        Pair<Integer, Integer> key = new Pair<>(head, relation);
        if (!head_relation2tail.containsKey(key)) {
            head_relation2tail.put(key, new HashSet<>());
        }
        Set<Integer> tail_set = head_relation2tail.get(key);
        tail_set.add(tail);
        if (!head2triple.containsKey(head)) {
            head2triple.put(head, new HashSet<>());
        }
        if (!tail2triple.containsKey(tail)) {
            tail2triple.put(tail, new HashSet<>());
        }
        Set<Integer> h_triple_set = head2triple.get(head);
        Set<Integer> t_triple_set = tail2triple.get(tail);
        h_triple_set.add(i);
        t_triple_set.add(i);
    }

    public void add2(int head, int tail) {
        fb_ht.add(head);
        fb_lt.add(tail);
        if (!et2type.containsKey(head)) {
            et2type.put(head, new HashSet<>());
        }
        Set<Integer> type_set = et2type.get(head);
        type_set.add(tail);
    }
    public void run(int nepoch, int nbatches) throws IOException {
        relation_vec = new double[relation_num][vector_len];
        entity_vec = new double[entity_num][vector_len];
        type_vec = new double[type_num][vector_len];
        A = new double[m][vector_len];
        connectconf = new double[fb_ht.size()];
        rateconf = new double[fb_ht.size()];
        for (int i = 0; i < fb_ht.size(); i++) {
            connectconf[i] = 1;
            rateconf[i] = 1;
        }
        for (int i = 0; i < relation_num; i++) {
            for (int j = 0; j < vector_len; j++) {
                relation_vec[i][j] = uniform(-6 / sqrt(vector_len), 6 / sqrt(vector_len));
            }
        }
        for (int i = 0; i < entity_num; i++) {
            for (int j = 0; j < vector_len; j++) {
                entity_vec[i][j] = uniform(-6 / sqrt(vector_len), 6 / sqrt(vector_len));
            }
            norm(entity_vec[i], vector_len);
        }
        for (int i = 0; i < type_num; i++) {
            for (int j = 0; j < vector_len; j++) {
                type_vec[i][j] = uniform(-6 / sqrt(vector_len), 6 / sqrt(vector_len));
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < vector_len; j++) {
                A[i][j] = uniform(-6 / sqrt(vector_len), 6 / sqrt(vector_len));
            }
        }
        bfgs(nepoch, nbatches);
    }
    // endregion
}
