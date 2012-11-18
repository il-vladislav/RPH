from __future__ import division
from confmat import BinaryConfusionMatrix
import methods

def quality_score(tp, tn, fp, fn):       
        return (tp+tn)/(tp+tn+10*fp+fn)

def compute_quality_for_corpus(corpus_dir):
        truth_dic = methods.read_classification_from_file(methods.add_slash(corpus_dir)+"!truth.txt")
        pred_dic = methods.read_classification_from_file(methods.add_slash(corpus_dir)+"!prediction.txt")
        bc1 = BinaryConfusionMatrix('SPAM', 'OK')
        bc1.compute_from_dicts(truth_dic, pred_dic)
        dict_score = bc1.as_dict()
        fn=dict_score['fn']
        tn=dict_score['tn']
        fp=dict_score['fp']
        tp=dict_score['tp']
        return quality_score(tp, tn, fp, fn), tp, tn, fp, fn
