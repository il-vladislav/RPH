def read_classification_from_file(path):
        myfile = open(path, "r")
        mydict = {}
        for line in myfile:
                x = line.split(" ")
                x[1]=x[1].replace("\n","")
                mydict[x[0]]=x[1]
        return mydict
