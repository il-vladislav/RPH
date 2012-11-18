# -*- coding: cp1252 -*-
from Corpus import Corpus
from basefilter import BaseFilter
from sys import stdout
from time import sleep
from bs4 import BeautifulSoup
from string import ascii_letters

import utils
import email
import os.path
import re
import pickle
import collections
import basefilter
import tokenizer
import sys
import methods
import random
import Bayesian

############
#INTRODUSION
############
#It is a pretty simple version of my filter. I started work on it with creating method's like "Check for common spammers pattern",
#contains "Number of words with no vowels", "Number of words with at least two of letters J, K, Q, X, Z", "Number of words with at least 15 characters",
#"Binary feature indicating whether the strings “From:” and “To:” were both present", e.t.c
#(Based on this work http://stat.wvu.edu/~dluo/CS791A/project_proposal.pdf).
#But when I writed it and check on training data set(461:SPAM, 153:HAM), all patterns triggered without any depending to human eyes.
#I visualized proportion words without vowels in spam and ham (http://goo.gl/XU3AA),(http://goo.gl/DnF6p), and its seems like neutral network needed
#to create working filter based on pattern like this. Dead end for me. 
############
#DESCRIPTION
############
#Filter already trained on data sets, have dicts with spamers and hamers list, and list of spam/ham email subjects. Dict's summ all trainigs.
#Filter already trained on data sets with Bayesian algorithm. Dicts sum only my big training
#When filter test data: then check first black list of spamers, white list of hamers, then subject spam/ham lists and then, if it is all new
#email message, start Bayesian algorithm.
###########
#TO DO
###########
#Write some algorithm to make work check_common_spamers_pattern module (https://github.com/il-vladislav/RPH/tree/master/SPAM)
#Try to ignore HTML code, when using Bayesian algorithm.
#There is much thing to do, Machine Learning never stop...
###########


class MyFilter:
        def __init__(self):
                self.path_bl = 'black_list.pickle'
                self.path_wl = 'white_list.pickle'
                self.path_ssl = 'spam_subject_list.pickle'
                self.path_hsl = 'ham_subject_list.pickle'
                
                self.black_list = {} #Email-addresses marked as SPAM
                self.white_list = {} #Email-addresses marked as OK
                self.spam_subject_list = {} #Email-subjects marked as SPAM
                self.ham_subject_list = {} #Email-subjects marked as OK
                
                self.truth = None #!trurh.txt dict
                
        def train(self,path_to_truth_dir):
                corpus = Corpus(path_to_truth_dir)
                #Read truth file
                truth = utils.read_classification_from_file(methods.add_slash(path_to_truth_dir)+"!truth.txt")
                #Make truth global
                self.truth = truth
                for fname, body in corpus.emails_as_string():
                        email_as_file = open(methods.add_slash(path_to_truth_dir) + fname,'r',encoding = 'utf-8')
                        #Read email with EMAIL parser
                        msg = email.message_from_file(email_as_file)
                        self.extract_senders_list(msg,fname)
                        self.check_subject(msg,fname)

                #Generate dict's
                methods.generate_file_from_dict(self.path_bl , self.black_list)
                methods.generate_file_from_dict(self.path_wl ,self.white_list)
                methods.generate_file_from_dict(self.path_ssl , self.spam_subject_list)
                methods.generate_file_from_dict(self.path_hsl ,self.ham_subject_list)
                #Bayesian already trained on big data set
                                
               
        def test(self, path_to_test_dir):
                predictions = {} #Predictions dict {fname:prediction}
                bs = Bayesian.Bayesian()
                corpus = Corpus(path_to_test_dir)
                #Read dict's (if test called before train)
                black_list_dict = methods.read_dict_from_file(self.path_bl)
                white_list_dict = methods.read_dict_from_file(self.path_wl)
                spam_subject_dict = methods.read_dict_from_file(self.path_ssl)
                ham_subject_dict = methods.read_dict_from_file(self.path_hsl)
                
                for fname, body in corpus.emails_as_string():
                        #Open email with parser
                        email_as_file = open(methods.add_slash(path_to_test_dir) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)

                        #Check if sender in a black list
                        if (self.extract_email_adress_from_text(msg['From']) in black_list_dict):
                                predictions[fname] = 'SPAM'
                        elif(self.extract_email_adress_from_text(msg['From']) in white_list_dict):
                        #Check if sender in a white list
                                predictions[fname] = 'OK'
                        #Check if subject in a black list
                        elif(self.extract_email_adress_from_text(msg['From']) in spam_subject_dict):
                             prediction[fname] = 'SPAM'
                        #Check if subject in a white list
                        elif(self.extract_email_adress_from_text(msg['From']) in ham_subject_dict):
                                prediction[fname] = 'OK'
                        #Run Bayesian checker
                        else:                
                                if (bs.bayesian_prediction(methods.get_text(msg))) > 0.485:
                                        predictions[fname] = 'SPAM'
                                else:
                                        predictions[fname] = 'OK'

                #Generate prediction file
                bf = BaseFilter(path_to_test_dir,predictions)
                bf.generate_prediction_file()

        def extract_senders_list(self, msg, fname):
                """
                Inputs: path to dir
                Outputs: none
                Effects: Extract email-adresses from email 'From', check if email is SPAM or HAM, generate two dictionaries {email : filename}
                """
                i = self.extract_email_adress_from_text(msg['From']) #we use this var as index, so name is 'i'
                if (self.truth[fname] == 'SPAM'):
                        self.black_list[i] = fname 
                elif (self.truth[fname] == 'OK'):
                        self.white_list[i] = fname
                                       

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
                
        def check_subject(self, msg, fname):
                """
                Inputs: path to dir
                Outputs: none
                Effects: Extract subjects from email 'Subject', check if email is SPAM or HAM, generate two dictionaries {email : subject}
                """     
                i = msg['Subject']
                if (self.truth[fname] == 'SPAM'):
                        self.spam_subject_list[i] = fname
                elif (self.truth[fname] == 'OK'):
                        self.ham_subject_list[i] = fname
                        
                
                
        
        


        
                

          
                
                
                
                                
              


                                
