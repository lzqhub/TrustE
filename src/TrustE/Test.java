package TrustE;
import java.io.*;
import java.util.*;

import static TrustE.GlobalValue.*;
import static TrustE.Gradient.calc_sum2;

public class Test {
    // region private members
    private List<Integer> fb_h;
    private List<Integer> fb_l;
    private List<Integer> fb_r;
    private List<Integer> fb_ht;
    private List<Integer> fb_lt;
    private Map<Pair<Integer, Integer>, Set<Integer>> head_relation2tail; // to save the (h, r, t)
    private Map<Integer, Set<Integer>> et2type;
    // endregion

    Test() {
        fb_h = new ArrayList<>();
        fb_l = new ArrayList<>();
        fb_r = new ArrayList<>();
        fb_ht = new ArrayList<>();
        fb_lt = new ArrayList<>();
        head_relation2tail = new HashMap<>();
        et2type = new HashMap<>();
    }

    public void add(int head, int relation, int tail, boolean flag) {
        /**
         * head_relation2tail用于存放 正确的三元组
         * flag=true 表示该三元组关系正确
         */
        if (flag) {
            TrustE.Pair<Integer, Integer> key = new Pair<>(head, relation);
            if (!head_relation2tail.containsKey(key)) {
                head_relation2tail.put(key, new HashSet<>());
            }
            Set<Integer> tail_set = head_relation2tail.get(key);
            tail_set.add(tail);
        } else {
            fb_h.add(head);
            fb_r.add(relation);
            fb_l.add(tail);
        }
    }

    public void add1(int head, int tail, boolean flag) {
        /**
         * flag=true, （entity,type）pair is true
         */
        if (flag) {
            if (!et2type.containsKey(head)) {
                et2type.put(head, new HashSet<>());
            }
            Set<Integer> tail_set = et2type.get(head);
            tail_set.add(tail);
        } else {
            fb_ht.add(head);
            fb_lt.add(tail);
        }
    }

    public static void Read_Vec_File(String file_name, double[][] vec, int veclen) throws IOException {
        File f = new File(file_name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
        String line;
        for (int i = 0; (line = reader.readLine()) != null; i++) {
            String[] line_split = line.split("\t");
            for (int j = 0; j < veclen; j++) {
                vec[i][j] = Double.valueOf(line_split[j]);
            }
        }
    }

    public static void Read_Vec_File(String file_name, double[] vec) throws IOException {
        File f = new File(file_name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
        String line;
        for (int i = 0; (line = reader.readLine()) != null; i++) {
            String line_split = line.replaceAll("\r|\n", "");
                vec[i] = Double.valueOf(line_split);
        }
    }

//    public static void Read_Vec_File(String file_name, double[][][] vec, int veclen) throws IOException {
//        File f = new File(file_name);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
//        String line;
//        for (int i = 0; (line = reader.readLine()) != null; i++) {
//            String[] line_split = line.split("\t");
//            int k = i/m;
//            for (int j = 0; j < veclen; j++) {
//                vec[k][i%m][j] = Double.valueOf(line_split[j]);
//            }
//        }
//    }

    private void relation_add(Map<Integer, Integer> relation_num, int relation) {
        if (!relation_num.containsKey(relation)) {
            relation_num.put(relation, 0);
        }
        int count = relation_num.get(relation);
        relation_num.put(relation, count + 1);
    }

    private void map_add_value(Map<Integer, Integer> tmp_map, int id, int value) {
        if (!tmp_map.containsKey(id)) {
            tmp_map.put(id, 0);
        }
        int tmp_value = tmp_map.get(id);
        tmp_map.put(id, tmp_value + value);
    }

    private boolean hrt_isvalid(int head, int relation, int tail, int id) {
        /**
         * 如果实体之间已经存在正确关系，则不需要计算距离
         * 如果头实体与尾实体一致，也排除该关系的距离计算
         */
        if (head == tail) {
            return true;
        }
        if(head==fb_h.get(id)&&relation==fb_r.get(id)&&tail==fb_l.get(id)){
            return false;
        }
        Pair<Integer, Integer> key = new Pair<>(head, relation);
        Set<Integer> values = head_relation2tail.get(key);
        if (values == null || !values.contains(tail)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hrt_isvalid1(int id, int tail) {
        if(tail==fb_lt.get(id)){
            return false;
        }
        Set<Integer> values = et2type.get(fb_ht.get(id));
        if (values == null || !values.contains(tail)) {
            return false;
        } else {
            return true;
        }
    }

    public void run() throws IOException {
        relation_vec = new double[relation_num][vector_len];
        entity_vec = new double[entity_num][vector_len];
        type_vec = new double[type_num][vector_len];
        A = new double[m][vector_len];
//        System.out.println(m);
        Read_Vec_File("TrustE/result/relation2vec.bern", relation_vec, vector_len);
        Read_Vec_File("TrustE/result/entity2vec.bern", entity_vec, vector_len);
        Read_Vec_File("TrustE/result/type2vec.bern", type_vec, vector_len);
        Read_Vec_File("TrustE/result/A.bern", A, vector_len);

        int lsum = 1, rsum = 1;
        int lp_n = 0, rp_n = 0;
        int sum_tprank = 1, sum_tphit10 = 0;
        double lmrr_et=0, rmrr_et=0,tpmrr=0;
        Map<Integer, Integer> lsum_r = new HashMap<>();
        Map<Integer, Integer> rsum_r = new HashMap<>();
        Map<Integer, Integer> lp_n_r = new HashMap<>();
        Map<Integer, Integer> rp_n_r = new HashMap<>();
        Map<Integer, Integer> rel_num = new HashMap<>();

        File out_file = new File("TrustE/result/output_detail.txt");
        File out_file1 = new File("TrustE/result/output_detail_type.txt");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(out_file), "UTF-8");
        OutputStreamWriter writer1 = new OutputStreamWriter(new FileOutputStream(out_file1), "UTF-8");

        System.out.printf("Total triple iterations = %s\n", fb_l.size());
        System.out.printf("Total double iterations = %s\n", fb_ht.size());
//        for (int id = 0; id < fb_l.size(); id++) {
//
//                int head = fb_h.get(id);
//                int tail = fb_l.get(id);
//                int relation = fb_r.get(id);
//                relation_add(rel_num, relation);
//                List<Pair<Integer, Double>> head_dist = new ArrayList<>();
//                for (int i = 0; i < entity_num; i++) {
//                    if (hrt_isvalid(i, relation, tail,id)) {
//                        continue;
//                    }
//                    double sum = calc_sum(i, tail, relation);
//                    head_dist.add(new Pair<>(i, sum));
//                }
//                Collections.sort(head_dist, (o1, o2) -> Double.compare(o1.b, o2.b));
//
//                for (int i = 0; i < head_dist.size(); i++) {
//                    int cur_head = head_dist.get(i).a;
//                    if (cur_head == head) {
//                        lsum += i; // 统计小于<h, l, r>距离的数量
//                        lmrr_et += 1/(i+1);
//                        map_add_value(lsum_r, relation, i);
//                        if (i <= 10) {
//                            lp_n++;
//                            map_add_value(lp_n_r, relation, 1);
//                        }
////                    String str = String.format("%s  %s  %s, dist=%f, %d\n\n", id2entity.get(head), id2relation.get(relation),
////                            id2entity.get(tail), calc_sum(head, tail, relation), i);
////                    writer.write(str);
////                    writer.flush();
//                        break;
//                    } else {
////                    String temp_str = String.format("%s  %s  %s, dist=%f, %d\n", id2entity.get(cur_head), id2relation.get(relation),
////                            id2entity.get(tail), calc_sum(cur_head, tail, relation), i);
////                    writer.write(temp_str);
////                    writer.flush();
//                    }
//                }
//
//            List<Pair<Integer, Double>> tail_dist = new ArrayList<>();
//            for (int i = 0; i < entity_num; i++) {
//                if (hrt_isvalid(head, relation, i,id)) {
//                    continue;
//                }
//                double sum = calc_sum(head, i, relation);
//                tail_dist.add(new Pair<>(i, sum));
//            }
//            Collections.sort(tail_dist, (o1, o2) -> Double.compare(o1.b, o2.b));
//            for (int i = 0; i < tail_dist.size(); i++) {
//                int cur_tail = tail_dist.get(i).a;
//                if (cur_tail == tail) {
//                    rsum += i;
//                    rmrr_et += 1/(i+1);
//                    map_add_value(rsum_r, relation, i);
//                    if (i <= 10) {
//                        rp_n++;
//                        map_add_value(rp_n_r, relation, 1);
//                    }
//                    break;
//                }
//            }
//        }

        for (int id = 0; id < fb_ht.size(); id++) {

            int head = fb_ht.get(id);
            int tail = fb_lt.get(id);
            List<Pair<Integer, Double>> type_dist = new ArrayList<>();
            for (int i = 0; i < type_num; i++) {
                if (hrt_isvalid1(id, i)) {
                    continue;
                }
                double sum = calc_sum2(head, i);
                type_dist.add(new Pair<>(i, sum));
            }
            Collections.sort(type_dist, (o1, o2) -> Double.compare(o1.b, o2.b));
            for (int i = 0; i < type_dist.size(); i++) {
                int cur_type = type_dist.get(i).a;
                if (cur_type == tail) {
                    sum_tprank += i; // 统计小于<h, l, r>距离的数量
                    tpmrr += 1/(i+1);
                    if (i <= 10) {
                        sum_tphit10++;
                    }
//                    String str = String.format("%s %s, dist=%f, %d\n\n", id2entity.get(head), id2type.get(tail), calc_sum2(head, tail), i);
//                    writer.write(str);
//                    writer.flush();
                    break;
                } else {
//                    String temp_str = String.format("%s %s, dist=%f, %d\n", id2entity.get(head), id2type.get(cur_type), calc_sum2(head, cur_type), i);
//                    writer.write(temp_str);
//                    writer.flush();
                }
            }

        }
        System.out.printf("lsum = %s, tail number = %s\n", lsum, fb_l.size());
        System.out.printf("left:\tMeanrank:\t%s\tHits@10:\t%s\tMRR:\t%s\n", (lsum * 1.0) / fb_l.size(), (lp_n * 1.0) / fb_l.size(), lmrr_et/fb_l.size());
        System.out.printf("right:\tMeanrank:\t%s\tHits@10:\t%s\tMRR:\t%s\n", (rsum * 1.0) / fb_h.size(), (rp_n * 1.0) / fb_h.size(),rmrr_et/fb_h.size());
        System.out.printf("type:\tMeanrank:\t%s\tHits@10:\t%s\tMRR:\t%s\n", (sum_tprank * 1.0) / fb_ht.size(), (sum_tphit10 * 1.0) / fb_ht.size(),tpmrr/fb_ht.size());

    }

}
