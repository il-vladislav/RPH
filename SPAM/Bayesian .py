from filter import MyFilter
import csv
import filter
def create_dic_from_cvs(path,fname):
        dic = {}
        with open(path, 'r') as csvfile:
                spamreader = csv.reader(csvfile, delimiter=' ', quotechar='|')
                for row in spamreader:
                        a = (row[0].split(','))
                        b = a[0]
                        b = b.replace('"', '').strip()
                        a = a[2]
                        dic[b]=a
                mf = MyFilter()
                mf.generate_file_from_dict(fname, dic)
                for i in dic:
                        print(i, dic[i])
