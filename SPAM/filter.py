import utils
import email
import Corpus
import re
import pickle
import collections
from Corpus import Corpus
import basefilter
from basefilter import BaseFilter

class MyFilter:
        def __init__(self):
                self.spamers = {}
                self.hamers = {}
                

        def train(self,path_to_truth_dir):
                self.FROM_emails_to_dict(path_to_truth_dir)

        def test(self, path_to_test_dir):
                prediction_dict = {}
                corpus = Corpus(path_to_test_dir)
                for fname, body in corpus.emails_as_string():
                        email_file = open(corpus.add_slash(path_to_test_dir)+fname,'r')
                        msg = email.message_from_file(email_file)
                        if(self.extract_email_adress_from_text(msg['From']) in self.spamers):
                                prediction_dict[fname] = 'SPAM'
                        else:
                                prediction_dict[fname] = 'OK'
                bf1 = BaseFilter(path_to_test_dir,prediction_dict)
                bf1.generate_prediction_file()


        
        def FROM_emails_to_dict(self,path):
                #hamers and spamers are dict with type 'str':'str'
                #fname : FROM_email
                #'1145.34a6c095d84e586da2b0177b7914882f' : 'robinderbains@shaw.ca'
                spamers = {}
                hamers = {}
                
                prediction = utils.read_classification_from_file(path, "!truth.txt")
                corpus = Corpus(path)
                
                for fname, body in corpus.emails_as_string():
                        email_file = open(corpus.add_slash(path)+fname,'r')
                        msg = email.message_from_file(email_file)
                        i = self.extract_email_adress_from_text(msg['From'])
                        if (prediction[fname] == 'SPAM'):
                                spamers[i] = fname
                        elif (prediction[fname] == 'OK'):
                                hamers[i] = fname
                self.spamers = spamers
                self.hamers = hamers
                
                self.generate_file_from_dict(path,'!spammers.txt', spamers)
                self.generate_file_from_dict(path,'!hamers.txt',hamers)

        


        def extract_email_adress_from_text(self, text):
                #input "Monty Solomon <monty@roscom.com>"
                #output "monty@roscom.com"
                try:
                        mailsrch = re.compile(r'[\w\-][\w\-\.]+@[\w\-][\w\-\.]+[a-zA-Z]{1,4}')
                        list_of_emails = mailsrch.findall(text)
                        if not list_of_emails:
                                return "None"
                        return list_of_emails[0]
                except TypeError:
                        return "None"

        def generate_file_from_dict(self,path, fname, my_dict):
                #input path, output filename and dict
                #output file path/filename with dict
                #C:/1/!spamers.txt with dict of spamers
                path_to_prediction_file = self.add_slash(path)+fname
                prediction_file = open(path_to_prediction_file, 'w+')
                pickle.dump(my_dict, prediction_file)
                prediction_file.close()

        def read_dict_from_file(self, path, fname):
                #reverse operation
                pkl_file = open(self.add_slash(path)+fname)
                my_dict = pickle.load(pkl_file)
                pkl_file.close()
                return my_dict
                
        def add_slash(self, path):
                if path.endswith("/"): return path
                return path + "/"


"""
def count_long_words_s(self,path):
                word_list_s = []
                word_list_h = []
                worda
                prediction = utils.read_classification_from_file(path, "!truth.txt")
                corpus = Corpus(path)
                for fname, body in corpus.emails_as_string():
                        email_file = open(corpus.add_slash(path)+fname,'r')
                        msg = email.message_from_file(email_file)
                        email_file.close()
                        try:
                                for words in msg.get_payload().split():
                                        if len(words) > 15:
                                                if (prediction[fname] == 'SPAM'):
                                                        word_list_s.append(words)
                                                if (prediction[fname] == 'OK'):
                                                        word_list_h.append(words)
                                                
                        except AttributeError:
                                pass
                for i in word_list_s:
                        if 
                return worda
"""
                
                        
                        
