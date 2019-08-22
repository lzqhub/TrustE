# TrustE
TrustE fcous on learning better embeddings of entities and entitiy types in a noisy environment. Our code implement TrustE model on two dataset(Freebase and YAGO)
Author：lizhiquan@smail.swufe.edu.cn
Environment:Java 1.8
The source codes are modified versions from original source code from [TransE](https://github.com/MaximTian/TransX)
## Usage instructions
### Datasets and related files
* 1. Freebase
          1.01 entity2id
          1.02 relation2id
          1.03 type2id
     1.04 train(FB15K)
     1.05 valid(FB15K)
     1.06 test(FB15K)
     1.07 FB15k_Entity_Type_train10(FB15KET-N1, noisy data is append directly behind the FB15k_Entity_Type_train)
     1.08 FB15k_Entity_Type_train20(FB15KET-N2)
     1.09 FB15k_Entity_Type_train40(FB15KET-N3)
     1.10 FB15k_Entity_Type_valid
     1.11 FB15k_Entity_Type_test
* 2. YAGO
  * 1.01 ent2id
  * 1.02 rel2id
  * 1.03 type2id
  * 1.04 train(YAGO43K)
  * 1.05 valid(YAGO43K)
  * 1.06 test(YAGO43K)
  * 1.07 YAGO43k_Entity_Type_train10(YAGO43KET-N1, noisy data is append directly behind the FB15k_Entity_Type_train)
  * 1.08 YAGO43k_Entity_Type_train20(YAGO43KET-N2)
  * 1.09 YAGO43k_Entity_Type_train40(YAGO43KET-N3)
  * 1.10 YAGO43k_Entity_Type_valid
  * 1.11 YAGO43k_Entity_Type_test
### Parameter setup
