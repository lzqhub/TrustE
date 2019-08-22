package TrustE;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static TrustE.GlobalValue.*;

public class TestRun {

    private static Test test;

    private static int Read_Data(String file_name, Map<String, Integer> data2id, Map<Integer, String> id2data) throws IOException {
        int count = 0;
        File f = new File(file_name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] split_data = line.split("\t");
            data2id.put(split_data[0], Integer.valueOf(split_data[1]));
            id2data.put(Integer.valueOf(split_data[1]), split_data[0]);
            count++;
        }
        return count;
    }

    private static void GlobalValueInit() {
        relation2id = new HashMap<>();
        entity2id = new HashMap<>();
        type2id = new HashMap<>();
        id2relation = new HashMap<>();
        id2entity = new HashMap<>();
        id2type = new HashMap<>();
        left_entity = new HashMap<>();
        right_entity = new HashMap<>();
        left_num = new HashMap<>();
        right_num = new HashMap<>();
    }

    private static void vec_add_value(Map<Integer, Map<Integer, Integer>> entity_map, int key, int value_k) {
        if (!entity_map.containsKey(key)) {
            entity_map.put(key, new HashMap<>());
        }
        Map<Integer, Integer> entity_value = entity_map.get(key);
        if (!entity_value.containsKey(value_k)) {
            entity_value.put(value_k, 0);
        }
        entity_value.put(value_k, entity_value.get(value_k) + 1);
    }

    private static void prepare() throws IOException {
        GlobalValueInit();
        entity_num = Read_Data("TrustE/resource/data/entity2id.txt", entity2id, id2entity);
        relation_num = Read_Data("TrustE/resource/data/relation2id.txt", relation2id, id2relation);
        type_num =  Read_Data("TrustE/resource/data/type2id.txt", type2id, id2type);

        File f = new File("TrustE/resource/data/test.txt");
        File f1 = new File("TrustE/resource/data/FB15k_Entity_Types/FB15k_Entity_Type_test.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(f1),"UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] split_data = line.split("\t");
            String head = split_data[0];
            String tail = split_data[1];
            String relation = split_data[2];
            if (!entity2id.containsKey(head)) {
                System.out.printf("miss entity: %s\n", head);
            }
            if (!entity2id.containsKey(tail)) {
                System.out.printf("miss entity: %s\n", tail);
            }
            if (!relation2id.containsKey(relation)) {
                relation2id.put(relation, relation_num);
                relation_num++;
            }
//            vec_add_value(left_entity, relation2id.get(relation), entity2id.get(head));
//            vec_add_value(right_entity, relation2id.get(relation), entity2id.get(tail));

            test.add(entity2id.get(head), relation2id.get(relation), entity2id.get(tail), false);
        }

        while ((line = reader1.readLine()) != null) {
            String[] split_data = line.split("\t");
            String head = split_data[0];
            String tail = split_data[1];
            if (!entity2id.containsKey(head)) {
                System.out.printf("miss entity: %s\n", head);
            }
            if (!type2id.containsKey(tail)) {
                System.out.printf("miss type: %s\n", tail);
            }
            test.add1(entity2id.get(head), type2id.get(tail), false);
        }

//        f = new File("TrustE/resource/data/YAGO/YAGO43K/train.txt");
//        File fv = new File("TrustE/resource/data/YAGO/YAGO43K/valid.txt");
//        File ft = new File("TrustE/resource/data/YAGO/YAGO43K/test.txt");
        f1 = new File("TrustE/resource/data/FB15k_Entity_Types/FB15k_Entity_Type_train.txt");
        File f2 = new File("TrustE/resource/data/FB15k_Entity_Types/FB15k_Entity_Type_valid.txt");
        File f3 = new File("TrustE/resource/data/FB15k_Entity_Types/FB15k_Entity_Type_test.txt");
//        reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
//        BufferedReader reader22 = new BufferedReader(new InputStreamReader(new FileInputStream(fv),"UTF-8"));
//        BufferedReader reader33 = new BufferedReader(new InputStreamReader(new FileInputStream(ft),"UTF-8"));
        reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(f1),"UTF-8"));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(f2),"UTF-8"));
        BufferedReader reader3 = new BufferedReader(new InputStreamReader(new FileInputStream(f3),"UTF-8"));
        while ((line = reader.readLine()) != null) {
            String[] split_data = line.split("\t");
            String head = split_data[0];
            String tail = split_data[1];
            String relation = split_data[2];
            if (!entity2id.containsKey(head)) {
                System.out.printf("miss entity: %s\n", head);
            }
            if (!entity2id.containsKey(tail)) {
                System.out.printf("miss entity: %s\n", tail);
            }
            if (!relation2id.containsKey(relation)) {
                relation2id.put(relation, relation_num);
                relation_num++;
            }
            test.add(entity2id.get(head), relation2id.get(relation), entity2id.get(tail), true);
        }

//        while ((line = reader22.readLine()) != null) {
//            String[] split_data = line.split("\t");
//            String head = split_data[0];
//            String tail = split_data[1];
//            String relation = split_data[2];
//            if (!entity2id.containsKey(head)) {
//                System.out.printf("miss entity: %s\n", head);
//            }
//            if (!entity2id.containsKey(tail)) {
//                System.out.printf("miss entity: %s\n", tail);
//            }
//            if (!relation2id.containsKey(relation)) {
//                relation2id.put(relation, relation_num);
//                relation_num++;
//            }
//            test.add(entity2id.get(head), relation2id.get(relation), entity2id.get(tail), true);
//        }
//        while ((line = reader33.readLine()) != null) {
//            String[] split_data = line.split("\t");
//            String head = split_data[0];
//            String tail = split_data[1];
//            String relation = split_data[2];
//            if (!entity2id.containsKey(head)) {
//                System.out.printf("miss entity: %s\n", head);
//            }
//            if (!entity2id.containsKey(tail)) {
//                System.out.printf("miss entity: %s\n", tail);
//            }
//            if (!relation2id.containsKey(relation)) {
//                relation2id.put(relation, relation_num);
//                relation_num++;
//            }
//            test.add(entity2id.get(head), relation2id.get(relation), entity2id.get(tail), true);
//        }

        while ((line = reader1.readLine()) != null) {
            String[] split_data = line.split("\t");
            String head = split_data[0];
            String tail = split_data[1];
            if (!entity2id.containsKey(head)) {
                System.out.printf("miss entity: %s\n", head);
            }
//            if (!entity2id.containsKey(tail)) {
//                System.out.printf("miss type: %s\n", tail);
//            }

            test.add1(entity2id.get(head), type2id.get(tail), true);
        }
        while ((line = reader2.readLine()) != null) {
            String[] split_data = line.split("\t");
            String head = split_data[0];
            String tail = split_data[1];
            if (!entity2id.containsKey(head)) {
                System.out.printf("miss entity: %s\n", head);
            }
//            if (!entity2id.containsKey(tail)) {
//                System.out.printf("miss type: %s\n", tail);
//            }

            test.add1(entity2id.get(head), type2id.get(tail), true);
        }
        while ((line = reader3.readLine()) != null) {
            String[] split_data = line.split("\t");
            String head = split_data[0];
            String tail = split_data[1];
            if (!entity2id.containsKey(head)) {
                System.out.printf("miss entity: %s\n", head);
            }
//            if (!entity2id.containsKey(tail)) {
//                System.out.printf("miss type: %s\n", tail);
//            }

            test.add1(entity2id.get(head), type2id.get(tail), true);
        }
        System.out.printf("entity number = %s\n", entity_num);
        System.out.printf("relation number = %s\n", relation_num);
        System.out.printf("type number = %s\n", type_num);
    }

    public static void test_run() throws IOException {
        test = new Test();
        prepare();
        test.run();
    }

}
