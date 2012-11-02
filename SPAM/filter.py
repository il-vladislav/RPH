import utils
import email
import Corpus
import re
import pickle
from Corpus import Corpus
import basefilter
from basefilter import BaseFilter

class MyFilter:
        def __init__(self):
                self.spamers = {}
                self.hamers = {}
                

        def train(self,path_to_truth_dir):
                from_dict = {}
                spamers = {}
                hamers = {}
                prediction = utils.read_classification_from_file(path_to_truth_dir, "!truth.txt")
                corpus = Corpus(path_to_truth_dir)
                for fname, body in corpus.emails_as_string():
                        email_file = open(corpus.add_slash(path_to_truth_dir)+fname,'r')
                        msg = email.message_from_file(email_file)
                        from_dict[self.extract_email_adress_from_text(msg['From'])] = prediction[fname]
                for i in from_dict:
                        if (from_dict[i] == 'SPAM'):
                                try:
                                        spamers[i] += 1
                                except KeyError:
                                        spamers[i] = 1
                        if (from_dict[i] == 'OK'):
                                try:
                                        hamers[i] += 1
                                except KeyError:
                                        hamers[i] = 1
                self.spamers = spamers
                self.hamers = hamers

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
                

        def extract_email_adress_from_text(self, text):
                mailsrch = re.compile(r'[\w\-][\w\-\.]+@[\w\-][\w\-\.]+[a-zA-Z]{1,4}')
                list_of_emails = mailsrch.findall(text)
                if not list_of_emails:
                        return "None"
                return list_of_emails[0]

        def generate_spamers_file(self,path_to_test_dir):
                path_to_prediction_file = self.add_slash(path_to_test_dir)+'!spamers.txt'
                prediction_file = open(path_to_prediction_file, 'w+')
                pickle.dump(self.spamers, prediction_file)
                prediction_file.close()

        def read_spamers_file(self, path_to_test_dir):
                pkl_file = open(self.add_slash(path_to_test_dir)+'!spamers.txt')
                mydict2 = pickle.load(pkl_file)
                pkl_file.close()
                return mydict2
                
        def add_slash(self, path):
                if path.endswith("/"): return path
                return path + "/"

                
                        
                        
