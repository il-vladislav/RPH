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
                self.spamicity = methods.read_dict_from_file('spamicity.pickle')
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
                #for word in msg1:
                        #a[word] = self.word_spamicity(word)
                for word in self.spamicity:
                        up = up*self.spamicity[word]
                        down = down*(1.0-(self.spamicity[word]-0.000000000000001))
                if (up == 0 or down == 0):
                        up = 0.5
                pred = up/(up+down)                
                return pred



        def study(self, path):
                counter = 0
                msg1 = []
                spamicity = {}
                hamicity = {}
                spam = {}
                ham = {}
                spam_c = 0
                ham_c = 0
                corpus = Corpus(path)
                truth = utils.read_classification_from_file(methods.add_slash(path)+"!truth.txt")
                for fname, body in corpus.emails_as_string():
                        counter  += 1
                        print(counter )
                        msg1 = []
                        email_as_file = open(methods.add_slash(path) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)
                        msg = methods.get_text(msg)
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
                                if truth[fname] == 'SPAM':
                                        spam_c += 1
                                        if word in spam:
                                                spam[word] += 1
                                        else:
                                                spam[word] = 1
                                elif truth[fname] == 'OK':
                                        ham_c += 1
                                        if word in ham:
                                                ham[word] += 1
                                        else:
                                                ham[word] = 1
                counter = 0
                for word in spam:
                        counter += 1
                        print(counter)
                        try:
                                ham_probability = ham[word] / ham_c
                        except KeyError:
                                ham_probability = 0.5
                        spam_probability = spam[word] / spam_c
                        spam_probability = 0.5
                        spamicity[word] = spam_probability / (spam_probability + ham_probability)
                methods.generate_file_from_dict('spamicity.pickle',spamicity)
                                
                        
                        
                

