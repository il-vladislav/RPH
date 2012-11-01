import utils
import email
import Corpus
import re
from Corpus import Corpus
import basefilter
from basefilter import BaseFilter

class MyFilter:
        def __init__(self,path_to_truth_dir):
                self.path_to_truth_dir = path_to_truth_dir if path_to_truth_dir else []
                self.spamers = {}
                self.hamers = {}
                

        def train(self):
                from_dict = {}
                spamers = {}
                hamers = {}
                prediction = utils.read_classification_from_file(self.path_to_truth_dir, "!truth.txt")
                corpus = Corpus(self.path_to_truth_dir)
                for fname, body in corpus.emails_as_string():
                        email_file = open(corpus.add_slash(self.path_to_truth_dir)+fname,'r')
                        msg = email.message_from_file(email_file)
                        from_dict[self.extract_email_adress_from_text(msg['From'])] = prediction[fname]
                for i in from_dict:
                        if (from_dict[i] == 'SPAM'):
                                spamers[i] = 1
                        if (from_dict[i] == 'OK'):
                                hamers[i] = 1
                self.spamers = spamers
                self.hamers = hamers

        def test(self, path):
                prediction_dict = {}
                corpus = Corpus(path)
                for fname, body in corpus.emails_as_string():
                        email_file = open(corpus.add_slash(path)+fname,'r')
                        msg = email.message_from_file(email_file)
                        if(self.extract_email_adress_from_text(msg['From']) in self.spamers):
                                prediction_dict[fname] = 'SPAM'
                        else:
                                prediction_dict[fname] = 'OK'
                bf1 = BaseFilter(path,prediction_dict)
                bf1.generate_prediction_file()
                

        def extract_email_adress_from_text(self, text):
                mailsrch = re.compile(r'[\w\-][\w\-\.]+@[\w\-][\w\-\.]+[a-zA-Z]{1,4}')
                list_of_emails = mailsrch.findall(text)
                if not list_of_emails:
                        return "None"
                return list_of_emails[0]
                
                        
                        
