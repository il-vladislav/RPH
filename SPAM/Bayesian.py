from Corpus import Corpus
import re
import csv
import utils
import tokenizer
import pickle
import methods
import email
import os

class Bayesian:
        def __init__(self):
                self.ham_dict = methods.read_dict_from_file('ham_df.pickle')
                self.spam_dict = methods.read_dict_from_file('spam_df.pickle')
                
        def create_dic_from_cvs(self,path,fname):
                dic = {}
                with open(path, 'r') as csvfile:
                        spamreader = csv.reader(csvfile, delimiter=' ', quotechar='|')
                        for row in spamreader:
                                a = (row[0].split(','))
                                b = a[0]
                                b = b.replace('"', '').strip()
                                a = a[2]
                                a =  float(a)
                                dic[b]=a
                        methods.generate_file_from_dict(fname, dic)
                        for i in dic:
                                print(i, type(dic[i]))

        def word_spamicity(self,word):
                if (word in self.ham_dict and word in self.spam_dict):
                        return self.spam_dict[word]/(self.spam_dict[word]+self.ham_dict[word])
                else:
                        return 0.5


        def bayesian_prediction(self,msg):
                a = {}
                msg1 = []
                up = 1.0
                down = 1.0
                msg = msg.replace('\n',' ')
                msg = msg.replace(',','')
                msg = msg.replace('.','')
                msg = msg.replace('"','')
                msg = msg.replace('(','')
                msg = msg.replace(')','')
                msg = msg.replace('!','')
                msg = msg.replace('?','')
                msg = msg.split(' ')
                for word in msg:
                        re.sub('[^A-Za-z0-9]+', '', word)
                        msg1.append(word.lower())                     
                for word in msg1:
                        a[word] = self.word_spamicity(word)
                for word in a:
                        up = up*a[word]
                        down = down*(1.0-(a[word]-0.000000000000001))
                if (up == 0 or down == 0):
                        up = 0.5
                pred = up/(up+down)                
                return pred
                                
                        
                        
                

