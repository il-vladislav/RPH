import methods
class BaseFilter:
    def __init__(self,path_to_dir, pred_dict):
        self.path_to_dir = path_to_dir if path_to_dir else []
        self.pred_dict = pred_dict if pred_dict else []
        
    def generate_prediction_file(self):
        path_to_prediction_file = methods.add_slash(self.path_to_dir)+'!prediction.txt'
        prediction_file = open(path_to_prediction_file, 'w+')
        for i in self.pred_dict.keys():
            prediction_file.write(i + " " + self.pred_dict[i] + "\n")

