class BinaryConfusionMatrix:
        def __init__(self, pos_tag, neg_tag):
                self.pos_tag = pos_tag if pos_tag else []
                self.neg_tag = neg_tag if neg_tag else []
                self.tp = 0
                self.tn = 0
                self.fp = 0
                self.fn = 0
                self.tf_matrix = {'tp':self.tp, 'tn':self.tn, 'fp':self.fp, 'fn':self.fn}

        def as_dict(self):
                return(self.tf_matrix)

        def update(self, truth, pred):
                if (self.pos_tag !=truth):
                        if (truth == pred):
                                self.tf_matrix['tp'] += 1
                        if (truth != pred):
                                self.tf_matrix['fp'] += 1
                if (self.pos_tag == truth):
                        if (truth != pred):
                                self.tf_matrix['tn'] += 1
                        if (truth == pred):
                                self.tf_matrix['fn'] += 1 

        def compute_from_dicts(truth_dict, pred_dict):
                return 1

        
