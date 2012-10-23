def quality_score(tp, tn, fp, fn):
        return (((tp/fp)/2)+((tn/fn)/2))

def compute_quality_for_corpus(corpus_dir):
        truth_dic = utils.read_classification_from_file(corpus_dir, "!truth.txt")
        pred_dic = utils.read_classification_from_file(corpus_dir, "!prediction.txt")
        BinaryConfusionMatrix.compute_from_dicts(truth_dic, pred_dic)
        dict_score = BinaryConfusionMatrix.as_dict()
        tn = dict_score['tn']
        tp = dict_score['tp']
        fp = dict_score['fp']
        fn = dict_score['fn']
        return quality_score(tp, tn, fp, fn)
