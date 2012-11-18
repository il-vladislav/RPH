from filter import MyFilter
import csv
import filter
import tokenizer
class Bayesian:
        def __init__(self):
                mf = MyFilter()
                self.ham_dict = mf.read_dict_from_file('ham_df.pickle')
                self.spam_dict = mf.read_dict_from_file('spam_df.pickle')

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
                        mf = MyFilter()
                        mf.generate_file_from_dict(fname, dic)
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
                mf = MyFilter()
                for word in msg:
                        if tokenizer.shortphrase(word):
                                msg1.append(word)                        
                for word in msg1:
                        a[word] = self.word_spamicity(word)
                for word in a:
                        up = up*a[word]
                        down = down*(1.0-a[word]+0.0000000000000001)
                try:
                        pred = up / (up+down)
                except ZeroDivisionError:
                        print('1.0')
                        pred = 1.0
                return pred
                

