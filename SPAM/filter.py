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


class MyFilter:
        def __init__(self):
                ###To quick and easy refactor###
                self.path_bl = 'black_list.txt'
                self.path_gl = 'gray_list.txt'
                self.path_ssl = 'spam_subject_list.txt'
                self.path_hsl = 'ham_subject_list.txt'
                
                self.black_list = {} #Email-addresses marked as SPAM
                self.gray_list = {} #Email-addresses marked as OK
                self.spam_subject_list = {} #Email-subjects marked as SPAM
                self.ham_subject_list = {} #Email-subjects marked as OK
                
                self.truth = None #!trurh.txt dict


                ###########DELETE###########
                self.a = 0
                self.b = 0
                self.c = 0 
                self.sa = 0 
                self.sb = 0
                self.sc = 0
                
        def train(self,path_to_truth_dir):
                corpus = Corpus(path_to_truth_dir)
                truth = utils.read_classification_from_file(methods.add_slash(path_to_truth_dir)+"!truth.txt")
                self.truth = truth
                for fname, body in corpus.emails_as_string():              
                        email_as_file = open(methods.add_slash(path_to_truth_dir) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)
                        self.extract_senders_list(msg,fname)
                        self.check_subject(msg,fname)
                        
                methods.generate_file_from_dict(self.path_bl , self.black_list)
                methods.generate_file_from_dict(self.path_gl ,self.gray_list)
                methods.generate_file_from_dict(self.path_ssl , self.spam_subject_list)
                methods.generate_file_from_dict(self.path_hsl ,self.ham_subject_list)

                
               
        def test(self, path_to_test_dir):
                bs = Bayesian.Bayesian() 
                corpus = Corpus(path_to_test_dir)
                predictions = {}
                dic = methods.read_dict_from_file(self.path_bl)
                dic1 = methods.read_dict_from_file(self.path_ssl)
                dic2 = methods.read_dict_from_file(self.path_gl)
                for fname, body in corpus.emails_as_string():
                        email_as_file = open(methods.add_slash(path_to_test_dir) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)
                        if (self.extract_email_adress_from_text(msg['From']) in dic):
                                predictions[fname] = 'SPAM'
                        elif(self.extract_email_adress_from_text(msg['From']) in dic2):
                                predictions[fname] = 'OK'
                        else:
                                if (fname == '01300.bcd95d40246e03dcfcb088ab69a9c953'):
                                        print('its else')
                                if (bs.bayesian_prediction(methods.get_text(msg))) > 0.5:
                                        predictions[fname] = 'SPAM'
                                else:
                                        predictions[fname] = 'OK'
                                        
                bf = BaseFilter(path_to_test_dir,predictions)
                bf.generate_prediction_file()
                

        def find_alphabetic_words(self, text):
                letters = ascii_letters
                letters_nd_term = letters + "?!,."
                return not any([set(text[:-1]).difference(letters),text[-1] not in letters_nd_term])

        def find_in_string(self, target, string):
                """
                Inputs: target string, string
                Outputs: number of target-strings in string
                Effects: none
                """
                counter = 0                
                i = string.find(target)
                if (i != -1 and i != 0):
                        while True:
                                i = string.find(target, i+1)
                                counter += 1
                                if (i == -1):
                                        break
                return (counter)

        def word_without_vowels(self, word):
                """
                Inputs: word
                Outputs: True or False
                Effects: check, if words without vowels ('thx' is True, 'hi' is False)
                """
                vowels = "aeiuo"
                consonant_counter = 0
                for letter in word:
                        if letter not in vowels:
                                consonant_counter += 1
                if consonant_counter == len(word):
                        return True
                return False

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
                        self.gray_list[i] = fname
                                       

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
                        
                
                
        
        


        
                

          
                
                
                
                                
              


                                
