from Corpus import Corpus

import csv
import utils
import tokenizer
import pickle
import methods
import email
import os

class Bayesian:
        def __init__(self):
                self.ham_dict = self.read_dict_from_file('ham_df.pickle')
                self.spam_dict = self.read_dict_from_file('spam_df.pickle')

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
                        self.generate_file_from_dict(fname, dic)
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
                         msg1.append(tokenizer.shortphrase(word))                     
                for word in msg1:
                        a[word] = self.word_spamicity(word)
                for word in a:
                        up = up*a[word]
                        down = down*(1.0-a[word]+0.0000000000000001)
                
                pred = up / (up+down)                
                return pred

        def read_dict_from_file(self,fname):
                """
                Inputs:  name of file with dictionary
                Outputs: dictionary from file
                Effects: read existing dictionary from file [run test() before train()]
                """                
                pkl_file = open(fname, 'rb')
                my_dict = pickle.load(pkl_file)
                pkl_file.close()
                return my_dict

        def get_text(self,msg):
                """
                Inputs: message (using email lib)
                Outputs: message body
                Effects: check if message is multipart and return body
                """                
                unicode = str
                text = ""
                html = None
                if msg.is_multipart():
                        for part in msg.get_payload():
                                if part.get_content_charset() is None:
                                    charset = 'utf-8'
                                else:
                                    charset = part.get_content_charset()
                                if part.get_content_type() == 'text/plain':
                                    text = unicode(part.get_payload().encode('utf8'))
                                if part.get_content_type() == 'text/html':
                                    html = unicode(part.get_payload().encode('utf8'))
                        if html is None:
                                return text.strip()
                        else:
                                return html.strip()
                else:
                    text = msg.get_payload()
                    return text

        def generate_file_from_dict(self, fname, my_new_dict):
                """                 
                Inputs: path to dir, file name ('!hamers.txt' for example) and new dictionary
                Outputs: none
                Effects: Generate new file with dictionary. Check if file exist and then fusion two dictionaries (existing and new).
                """
                mfile = fname
                if os.path.exists(mfile):
                        mfile = open(fname,'rb')
                        my_existing_dict = pickle.load(mfile)
                        my_new_dict = my_new_dict.copy()
                        my_new_dict.update(my_existing_dict)                        
                        mfile.close()                
                mfile = open(fname, 'wb+')
                pickle.dump(my_new_dict, mfile)
                mfile.close()

        def study(self, path):
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
                        email_as_file = open(methods.add_slash(path) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)
                        msg = self.get_text(msg)
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
                                msg1.append(tokenizer.shortphrase(word)) 
                        for word in msg1:
                                if truth[fname] == 'SPAM':
                                        spam_c += 1
                                        if tokenizer.shortphrase(word) in spam:
                                                spam[tokenizer.shortphrase(word)] += 1
                                        else:
                                                spam[tokenizer.shortphrase(word)] = 1
                                elif truth[fname] == 'OK':
                                        ham_c += 1
                                        if tokenizer.shortphrase(word) in ham:
                                                ham[tokenizer.shortphrase(word)] += 1
                                        else:
                                                ham[tokenizer.shortphrase(word)] = 1
                for word in spam:
                        try:
                                ham_probability = ham[word] / ham_c
                        except KeyError:
                                ham_probability = 0.5
                        spam_probability = spam[word] / spam_c
                        spam_probability = 0.5
                        spamicity[word] = spam_probability / (spam_probability + ham_probability)
                self.generate_file_from_dict('spamicity.pickle',spamicity)
                                
                        
                        
                

