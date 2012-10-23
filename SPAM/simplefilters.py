import random
import basefilter
from basefilter import BaseFilter
import os

def NaiveFilter(path):
        pred_dict = {}
        for file_name in os.listdir(path):
                if not file_name.startswith("!"):
                        pred_dict[file_name]='OK'
        bf = BaseFilter(path,pred_dict)
        bf.generate_prediction_file()
        

def ParanoidFilter(path):
        pred_dict = {}
        for file_name in os.listdir(path):
                if not file_name.startswith("!"):
                        pred_dict[file_name]='SPAM'
        bf = BaseFilter(path,pred_dict)
        bf.generate_prediction_file()

        
def RandomFilter(path):
        pred_dict = {}
        for file_name in os.listdir(path):
                if not file_name.startswith("!"):
                        pred_dict[file_name]=random.choice(['OK','SPAM'])
        bf = BaseFilter(path,pred_dict)
        bf.generate_prediction_file()
