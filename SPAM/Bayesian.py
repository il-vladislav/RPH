from Corpus import Corpus
import re
import csv
import pickle
import methods
import email
import os

class Bayesian:
        def __init__(self):
                #Dicts with words from spam and ham
                self.ham_dict = methods.read_dict_from_file('ham_df.pickle')
                self.spam_dict = methods.read_dict_from_file('spam_df.pickle')                
        
        def word_spamicity(self,word):
                """
                Inputs: word
                Outputs: spamicity based on Bayes' Formula
                """
                if (word in self.ham_dict and word in self.spam_dict):
                        return self.spam_dict[word]/(self.spam_dict[word]+self.ham_dict[word])
                else:
                        return 0.5


        def bayesian_prediction(self,msg):
                """
                Inputs: message body
                Outputs: spamicity of message based on Bayes' Formula
                """
                words_bayes_prediction = {} #Prediction dict
                tokenizer_words_list = [] #Tokenizered words in list
                #Formula http://goo.gl/XRM56
                up = 1.0 #Part of Bayes' Formula 
                down = 1.0 #Part of Bayes' Formula
                #REGEX                
                msg = msg.split(' ')
                for word in msg:
                        re.sub('[^A-Za-z0-9]+', '', word)
                        #Add words to list
                        tokenizer_words_list.append(word.lower())                     
                for word in tokenizer_words_list:
                        #Calculate Bayes' prediction for words
                        words_bayes_prediction[word] = self.word_spamicity(word)
                #Calculate Bayes' prediction for message
                for word in words_bayes_prediction:
                        up = up*words_bayes_prediction[word]
                        down = down*(1.0-(words_bayes_prediction[word]-0.000000000000001))
                if (up == 0 or down == 0):
                        up = 0.5
                pred = up/(up+down)
                return pred
                                
                        
                        
                

