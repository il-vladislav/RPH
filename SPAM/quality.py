from __future__ import division
import utils
from confmat import BinaryConfusionMatrix

def quality_score(tp, tn, fp, fn):
        return (tp + tn) / (tp + tn + (10) * fp + fn)

def compute_quality_for_corpus(corpus_dir):
        truth_dic = utils.read_classification_from_file(corpus_dir, "!truth.txt")
        pred_dic = utils.read_classification_from_file(corpus_dir, "!prediction.txt")
        bc1 = BinaryConfusionMatrix('OK', 'SPAM')
        bc1.compute_from_dicts(truth_dic, pred_dic)
        dict_score = bc1.as_dict()
        tn = dict_score['tn']
        tp = dict_score['tp']
        fp = dict_score['fp']
        fn = dict_score['fn']
        return quality_score(tp, tn, fp, fn)
