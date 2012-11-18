from string import ascii_letters
import email
import os.path
import re
import pickle
import collections
import sys
import random

def get_text(msg):
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
                                       
def generate_file_from_dict(fname, my_new_dict):
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

def read_dict_from_file(fname):
        """
        Inputs:  name of file with dictionary
        Outputs: dictionary from file
        Effects: read existing dictionary from file
        """                
        pkl_file = open(fname, 'rb')
        my_dict = pickle.load(pkl_file)
        pkl_file.close()
        return my_dict

def read_classification_from_file(path):
        """
        Inputs:  path to file with dict
        Outputs: dictionary from file
        Effects: read existing dictionary from file
        """  
        myfile = open(path, "r")
        mydict = {}
        for line in myfile:
                x = line.split(" ")
                x[1]=x[1].replace("\n","")
                mydict[x[0]]=x[1]
        return mydict

        
def add_slash(path):
        """
        Inputs: path to dir
        Outputs: path to dir with slash
        Effects: Check if path to dir with slash or not, add slash
        """
        if path.endswith("/"): return path
        return path + "/"

def shortphrase(shortphrase):
        """
        Inputs: string
        Outputs: list contains rods with lowel letters, splited
        """
        if type(shortphrase) == str:
                shortphrase = shortphrase.translate(str.maketrans('.',' '))
                tokens = shortphrase.lower().split()
                return tokens
        return ""
