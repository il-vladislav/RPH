from Corpus import Corpus
from basefilter import BaseFilter

import utils
import email
import os.path
import re
import pickle
import collections
import basefilter
import tokenizer

class MyFilter:
        def __init__(self):
                #TO DO :var names!!!!
                self.senders_spam = {}
                self.senders_ham = {}
                self.subjects_spam = {}
                self.subjects_ham = {}

        def train(self,path_to_truth_dir):
                self.extract_senders_list(path_to_truth_dir)
                self.check_subject(path_to_truth_dir)
               
        def test(self, path_to_test_dir):
                pass

        def check_for_common_spammer_patters(self, path):               
                corpus = Corpus(path)
                for fname, body in corpus.emails_as_string():
                        email_as_file = open(corpus.add_slash(path) + fname,'r', encoding='utf-8')
                        msg = email.message_from_file(email_as_file)

                        #######################################################################
                        #Subject vars
                        subject_contains_repeated_letters = None
                        count_words_without_vowels = 0  
                        count_words_with_two_JKQXZ = 0
                        count_words_with_15_symbol = 0
                        count_words_only_uppercase = 0

                        #######################################################################
                        #Content type vars                                                    
                        content_type_text_html = None
                        message_priority = None

                        #######################################################################
                        #Body vars
                        words_without_vowels_body_counter = 0
                        
                        
                        two_letters = "jkqxz"
                        uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        
                        #######################################################################
                        #Check for common spammer patters from subject header
                        #######################################################################
                        
                        #Number of words with all letters in uppercase
                        for word in msg['Subject']:
                                uppercase_counter = 0
                                for letter in word:
                                        if letter in uppercase:
                                                uppercase_counter += 1
                                if uppercase_counter == len(word):
                                        count_words_only_uppercase += 1

                        #Tokenize subject to string with all lower letters
                        for word in tokenizer.shortphrase(msg['Subject']):
                                two_letters_counter = 0
                                for letter in word:
                                        #Count 'J K Q X Z' letters in word
                                        if letter in two_letters:
                                                two_letters_counter += 1
                                
                                #Is word with no vowels?     
                                if word_without_vowels(word): 
                                        count_words_without_vowels += 1
                                
                                #Is words with at least two of letters J, K, Q, X, Z?
                                if two_letters_counter >= 2:
                                        count_words_with_two_JKQXZ += 1
                                
                                #Is word with at least 15 characters
                                if len(word) >= 15:
                                        count_words_with_15_symbol += 1

                                #Binary feature indicating 3 or more repeated characters
                                if re.search(r'(.)\1\1', word):
                                        cont_words_with_repeat_sym = True

                        #######################################################################
                        #Check for common spammer patters from Content-Type header and Priority
                        #######################################################################
                        for word in tokenizer.shortphrase(msg['Content-Type']):
                                #Binary feature indicating the content type had been set to “text/html”
                                if word == ('text/html'):
                                        content_type_text_html = True

                        #Binary feature indicating whether the priority had  been set to any level not None TODO : Do something here!
                        for word in tokenizer.shortphrase(msg['Pirority']):
                                if word != None:
                                        message_priority = True

                        #######################################################################
                        #Check for common spammer patters from body
                        #######################################################################
                        for word in tokenizer.shortphrase(msg.get_payload()):
                                #Counter of alphabetic words with no vowels and at least 7 characters 
                                if len(word)<7:
                                        if word_without_vowels_checker(word):
                                                words_without_vowels_body_counter += 1
                                                
                                #Counter of alphabetic words with at least two of letters J, K, Q, X, Z 
                                

                        
                                        

        def word_without_vowels(self, word):
                vowels = "aeiuo"
                consonant_counter = 0
                for letter in word:
                        if letter not in vowels:
                                consonant_counter += 1
                if consonant_counter == len(word):
                        return True
                else return False

        def extract_senders_list(self, path):
                """
                Inputs: path to dir
                Outputs: none
                Effects: Extract email-adresses from email 'From', check if email is SPAM or HAM, generate two dictionaries {email : filename}
                """
                truth = utils.read_classification_from_file(self.add_slash(path)+"!truth.txt")
                corpus = Corpus(path)
                for fname, body in corpus.emails_as_string():
                        email_as_file = open(corpus.add_slash(path) + fname,'r')
                        msg = email.message_from_file(email_as_file)
                        i = self.extract_email_adress_from_text(msg['From']) #we use this var as index, so name is 'i'
                        if (truth[fname] == 'SPAM'):
                                self.senders_spam[i] = fname
                        elif (truth[fname] == 'OK'):
                                self.senders_ham[i] = fname
                self.generate_file_from_dict(path,'!spamers.txt', self.senders_spam)
                self.generate_file_from_dict(path,'!hamers.txt',self.senders_ham)
                

        def extract_email_adress_from_text(self, text):
                """
                Inputs: text "Monty Solomon <monty@roscom.com>"
                Outputs: email address "monty@roscom.com"
                """
                try:
                        mailsrch = re.compile(r'[\w\-][\w\-\.]+@[\w\-][\w\-\.]+[a-zA-Z]{1,4}')
                        list_of_emails = mailsrch.findall(text)
                        if not list_of_emails:
                                return "None"
                        return list_of_emails[0]
                except TypeError:
                        return "None"

        def generate_file_from_dict(self, path, fname, my_new_dict):
                """                 
                Inputs: path to dir, file name ('!hamers.txt' for example) and new dictionary
                Outputs: none
                Effects: Generate new file with dictionary. Check if file exist and then fusion two dictionaries (existing and new).
                """
                mfile = self.add_slash(path)+fname
                if os.path.exists(mfile):
                        mfile = open(mfile)
                        my_existing_dict = pickle.load(mfile)
                        my_new_dict = dict(my_existing_dict.items() + my_new_dict.items())
                mfile = open(self.add_slash(path)+fname, 'w+')
                pickle.dump(my_new_dict, mfile)
                mfile.close()

        def read_dict_from_file(self, path, fname):
                """
                Inputs: path to dir, name of file with dictionary
                Outputs: dictionary from file
                Effects: read existing dictionary from file [run test() before train()]
                """                
                pkl_file = open(self.add_slash(path)+fname)
                my_dict = pickle.load(pkl_file)
                pkl_file.close()
                return my_dict

        def check_subject(self, path):
                """
                Inputs: path to dir
                Outputs: none
                Effects: Extract subjects from email 'Subject', check if email is SPAM or HAM, generate two dictionaries {email : subject}
                """
                truth = utils.read_classification_from_file(self.add_slash(path)+"!truth.txt")
                corpus = Corpus(path)
                for fname, body in corpus.emails_as_string():
                        email_as_file = open(corpus.add_slash(path) + fname,'r')
                        msg = email.message_from_file(email_as_file)
                        i = msg['Subject']
                        if (truth[fname] == 'SPAM'):
                                self.subjects_spam[i] = fname
                        elif (truth[fname] == 'OK'):
                                self.subjects_ham[i] = fname
                self.generate_file_from_dict(path,'!subject_spam.txt', self.subjects_spam) #TO DO : name of file!!!
                self.generate_file_from_dict(path,'!subject_ham.txt',self.subjects_ham)
                

        def add_slash(self, path):
                """
                Inputs: path to dir
                Outputs: path to dir with slash
                Effects: Check if path to dir with slash or not, add slash
                """
                if path.endswith("/"): return path
                return path + "/"

 #TO DO Number of words with non-English characters, special characters such as punctuation, or digits at beginning or middle of word         
        """def word_with_digits_checker(self, word):
                begin_searcher = re.compile(r'[0-9]+[\w\-]')
                middle_searcher = re.compile(r'[\w\-]+[0-9]+[\w\-]')
                both_checker = re.compile(r'[0-9]+[\w\-]+[0-9]+[\w\-]')"""      
                
                
                
                                
              


                                
