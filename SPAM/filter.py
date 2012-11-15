# -*- coding: cp1252 -*-
from Corpus import Corpus
from basefilter import BaseFilter
from sys import stdout
from time import sleep
from bs4 import BeautifulSoup
from string import ascii_letters

import matplotlib.pyplot as plt
import time
import utils
import email
import os.path
import re
import pickle
import collections
import basefilter
import tokenizer
import sys
import addslash


class MyFilter:
        def __init__(self):
                ###To quick and easy refactor###
                self.path_bl = '!black_list.pickle'
                self.path_gl = '!gray_list.pickle'
                self.path_ssl = '!spam_subject_list.pickle'
                self.path_hsl = '!ham_subject_list.pickle'
                
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
                truth = utils.read_classification_from_file(addslash.add_slash(path_to_truth_dir)+"!truth.txt")
                self.truth = truth
                for fname, body in corpus.emails_as_string():              
                        email_as_file = open(addslash.add_slash(path_to_truth_dir) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)
                        self.extract_senders_list(msg,fname)
                        self.check_subject(msg,fname)
                        
                self.generate_file_from_dict(self.path_bl , self.black_list)
                self.generate_file_from_dict(self.path_gl ,self.gray_list)
                self.generate_file_from_dict(self.path_ssl , self.spam_subject_list)
                self.generate_file_from_dict(self.path_hsl ,self.ham_subject_list)

                
               
        def test(self, path_to_test_dir):
            predictions = {}
            dic = read_dict_from_file(self.path_bl)
            dic1 = read_dict_from_file(self.path_ssl)
            for fname, body in corpus.emails_as_string():
                        email_as_file = open(addslash.add_slash(path_to_test_dir) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)
                        if ((extract_email_adress_from_text(msg['From']) in dic.values())or msg['Subject'] in dic1.values()):
                            predictions[fname] = 'SPAM'
                        else:
                            predictions[fname] = 'OK'
            bf = BaseFilter(path_to_test_dir,predictions)
            bf.generate_prediction_file()
                        
            """
                #######TESTING VARS##
                ok = 0;
                ok_counter = 0;
                spam = 0;               
                spam_counter = 0;
                s=0;
                o=0;
                xs_series = []
                ys_series = []
                xh_series = []
                yh_series = []
                sd = {}
                hd = {}
                #####################

                
                prediction = "SPAM";
                corpus = Corpus(path_to_test_dir)
                truth = utils.read_classification_from_file(addslash.add_slash(path_to_test_dir)+"!truth.txt")
                self.truth = truth
                gh = 0
                hg = 0
                for fname, body in corpus.emails_as_string():
                        email_as_file = open(addslash.add_slash(path_to_test_dir) + fname,'r',encoding = 'utf-8')
                        msg = email.message_from_file(email_as_file)
                        soup = BeautifulSoup(self.get_text(msg))
                        a,b,c,d,e,f,g,h,i,j = self.check_for_common_spammer_patters(msg, fname)
                        #print(self.check_for_common_spammer_patters(msg), truth[fname])
                        if(truth[fname]=='OK'):
                            gh += 1
                            if h in hd:
                                hd[h] += 1
                            else:
                                hd[h] = 1
                        if(truth[fname]=='SPAM'):
                            hg += 1
                            if h in sd:
                                sd[h] += 1
                            else:
                                sd[h] = 1
                        try:
                            if (self.a/gh > self.sa/hg):
                                try:
                                    print(self.a/gh, self.b/gh,self.c/gh, 'OK')
                                except ZeroDivisionError:
                                    print("ZERO")
                                try:
                                    print(self.sa/hg, self.sb/hg,self.sc/hg, 'SPAM')
                                except ZeroDivisionError:
                                    print('ZERO')
                        except ZeroDivisionError:
                                    print('')
                        try:
                                 a = soup.font['color']
                        except (TypeError,KeyError):
                                a = None

                        try:
                                 b = soup.body['color']
                        except (TypeError,KeyError):
                                b= None
                        if (b != None or a != None):
                                #print(a,b)
                                pass
                hd = collections.OrderedDict(sorted(hd.items()))
                sd = collections.OrderedDict(sorted(sd.items()))

                for i in sd:
                        xs_series.append(i)
                        ys_series.append(sd[i])
                for i in hd:
                        xh_series.append(i)
                        yh_series.append(hd[i])
                        
                plt.gca().set_color_cycle(['red', 'blue'])
                plt.plot(xs_series, ys_series)
                plt.plot(xh_series, yh_series)                
                plt.legend(['SPAM', 'HAM'], loc='upper left')
                plt.show()
                print(self.a,self.b,self.c,gh)
                print(self.sa,self.sb,self.sc,hg)
                try:
                    print(self.a/gh, self.b/gh,self.c/gh, 'OK')
                except ZeroDivisionError:
                    print("ZERO")
                try:
                    print(self.sa/hg, self.sb/hg,self.sc/hg, 'SPAM')
                except ZeroDivisionError:
                    print('ZERO')"""
                
        
        def check_for_common_spammer_patters(self, msg, fname):               
                #######################################################################
                #Subject vars
                subject_contains_repeated_letters = False
                count_words_without_vowels = 0  
                count_words_with_two_JKQXZ = 0
                count_words_with_15_symbol = 0
                count_words_only_uppercase = 0

                #######################################################################
                #Content type vars                                                    
                content_type_text_html = False
                message_priority = False

                #######################################################################
                #Body vars
                words_without_vowels_body_counter = 0
                Number_of_HTML_opening_comment_tags = 0
                alphabetic_words_counter = 0
                count_words_with_at_lest_two_JKQXZ = 0
                count_alphabetic_words_15_long = 0
                
                words_without_vowels_proportion = 0
                words_with_at_lest_two_JKQXZ_proportion = 0
                alphabetic_words_15_long_proportion = 0
                
                from_equals_to = False
                
                two_letters = "jkqxz"
                uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        
                #######################################################################
                #Check for common spammer patters from subject header
                #######################################################################
                        
                
                #Number of words with all letters in uppercase
                if msg['Subject']:
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
                        if self.word_without_vowels(word): 
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
                        if word == ('text/html' or 'text/html;'):
                                content_type_text_html = True

                #Binary feature indicating whether the priority had  been set to any level not None
                for word in tokenizer.shortphrase(msg['Pirority']):
                        if word != None:
                                message_priority = True

                #######################################################################
                #Check for common spammer patters from body
                #######################################################################
                JKQXZ = 0 #counter of JKQZX letters in word
                l_counter = 0 #counter of letters in word
                for word in tokenizer.shortphrase(self.get_text(msg)):
                    if self.find_alphabetic_words:
                        alphabetic_words_counter +=1
                        #Counter of alphabetic words with no vowels and at least 7 characters
                        if len(word)<7:
                                if self.word_without_vowels(word):
                                                words_without_vowels_body_counter += 1                                                
                        #Counter of alphabetic words with at least two of letters J, K, Q, X, Z
                        for letter in word:
                            l_counter +=1
                            if letter in two_letters:
                                JKQXZ += 1
                         #Counter of alphabetic words with at least 15 letters
                        count_alphabetic_words_15_long += 1
                if JKQXZ >1:
                    count_words_with_at_lest_two_JKQXZ += 1
                #Proportion of alphabetic words with no vowels and at least 7 characters
                if (self.truth[fname] == 'OK'):
        
                    try:
                        words_without_vowels_proportion = words_without_vowels_body_counter/alphabetic_words_counter
                        self.a += words_without_vowels_proportion 
                    except ZeroDivisionError:
                        self.a +=  0
                    try:
                        words_with_at_lest_two_JKQXZ_proportion = count_words_with_at_lest_two_JKQXZ/alphabetic_words_counter
                        self.b += words_with_at_lest_two_JKQXZ_proportion 
                    except ZeroDivisionError:
                        self.b +=  0
                    
                    try:
                        alphabetic_words_15_long_proportion = count_alphabetic_words_15_long/alphabetic_words_counter
                        self.c += alphabetic_words_15_long_proportion 
                    except ZeroDivisionError:
                        self.c +=  0
        
                elif (self.truth[fname] == 'SPAM'):
        
                    try:
                        words_without_vowels_proportion = words_without_vowels_body_counter/alphabetic_words_counter
                        self.sa += words_without_vowels_proportion 
                    except ZeroDivisionError:
                        self.sa +=  0
                    try:
                        words_with_at_lest_two_JKQXZ_proportion = count_words_with_at_lest_two_JKQXZ/alphabetic_words_counter
                        self.sb += words_with_at_lest_two_JKQXZ_proportion
                    except ZeroDivisionError:
                        self.sb =  0
                    
                    try:
                        alphabetic_words_15_long_proportion = count_alphabetic_words_15_long/alphabetic_words_counter
                        self.sc += alphabetic_words_15_long_proportion 
                    except ZeroDivisionError:
                        self.sc +=  0
                
                #print(words_without_vowels_proportion,words_with_at_lest_two_JKQXZ_proportion,alphabetic_words_15_long_proportion,self.truth[fname])
                
                #######################################################################
                #Check for common non-spammer patters from FROM and TO
                #######################################################################
                FROM = tokenizer.shortphrase(self.extract_email_adress_from_text(msg['From']))
                TO = tokenizer.shortphrase(self.extract_email_adress_from_text(msg['To']))
                if (FROM==TO):
                        from_equals_to = True

                #######################################################################
                #HTML
                #######################################################################
                Number_of_HTML_opening_comment_tags = self.find_in_string('<!--',' '.join(tokenizer.shortphrase((self.get_text(msg)))))
                Number_of_hyperlinks = self.find_in_string('href=',' '.join(tokenizer.shortphrase((self.get_text(msg)))))
                """White_text = ['3D#ff0000', 'red', '3D"#000000"', '3D"#FF0000"', '3D"blue"', '#333333', '3D"#ffffff"', '#ffff80', '3D"#0000FF"', '#000000', '3D#000000', '3D"white"', '#800000', '3D#000000', '3D#0000ff', '3D"blue"', 'red', '#ff0000', '3D"#0000FF"', '3D"blue"', '3D"blue"', '#FF0033', '3D#000000', '#0000FF', '202498', '3D"#FFFFFF"', '#999999', '3D"#000066"', '000000', '#ff6600', '3D#000000', '#ff0000', '3D"#000066"', '3D#ffffff', '3D"#33333=', '3D#FF0000', '#999999', '3D"#00FF00"', 'black', '#fd0000', '#ff6600', 'black', '3D"#000066"', '#333366', '#FFFF00', '3D=22=23FF0000=22', '#FFFFFF', '3D"#008000"', '#2e4361', '3D"#000000"', 'black', 'black', 'black', '#666666', '3D"#FFFFFF"', '#666666', '#333333', '3D#000000', 'blue', '#333333', '#000000', '3D"#FFFF00"', '3D"#CC3333"', '3D"#FFFFFF"', '#000000', '3D"#FFFFFF"', '#999999', '#ff0000', '#ff0000', '#ff0000', '3D"#000066"', '3D"#FFFFFF"', '#FF0000', '3D"#000066"', '3D#ff0000', '3D"ED1C24"', '3D"ED1C24"', '3D#000000', '#666666', '3D"#FFFFFF"', '#000000', 'Firebrick', '#FFFFFF', '#3333FF', '#33CC99', '3D=22=23990000=22', '3D=23ffffff', '#000080', '#ff0000', '#000000', '3D"#66FF00"', '#000000', '#000080', '3D"#99ffff"', '#000000', 'gray', '3D=23ffffff', '#FFFFFF', 'gray', '#ffffff', '3Dred', '3D"#FF0000"', '#000000', '#000000', '3D=22=23=', '3D"#000080"', '#0000FF', '#000080', '3D=23ffffff', '#294D7F', '3D=23ffffff', '#000080', '#FFFFFF', '#FF0000', '3D#000000', '3D"#000066"', '#ff8080', '3D"#000080"', '3D"#0033=', '#ffffff', 'Firebrick', '#FF0000', '3D"487EB3"', '3D#000000', '3D#000000', '3D"#ffffff"', '#ff0000', 'Silver', '#FF0000', '3D"#333333"', '#660000']
                soup = BeautifulSoup(self.get_text(msg))
                a = 'none'
                for i in White_text:
                        try:
                                a = soup.font['color']
                        except (KeyError,TypeError):
                                pass
                        
                        if a == i:"""

                white_text = 1
                return(subject_contains_repeated_letters, count_words_without_vowels,count_words_with_two_JKQXZ,count_words_with_15_symbol,count_words_only_uppercase,content_type_text_html,message_priority,words_without_vowels_body_counter,from_equals_to,white_text)


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
                

         #TO DO Number of words with non-English characters, special characters such as punctuation, or digits at beginning or middle of word         
        """def word_with_digits_checker(self, word):
                begin_searcher = re.compile(r'[0-9]+[\w\-]')
                middle_searcher = re.compile(r'[\w\-]+[0-9]+[\w\-]')
                both_checker = re.compile(r'[0-9]+[\w\-]+[0-9]+[\w\-]')"""      
                
                
                
                                
              


                                
