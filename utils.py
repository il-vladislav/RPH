def read_classification_from_file(path):
	path = add_slash(path) + "!truth.txt"
	myfile = open(path, "r")
	mydict = {}
        for line in myfile.xreadlines():
                x= line.split(" ")
                x[1]=x[1].replace("\n","")
                mydict[x[0]]=x[1]
        return mydict
	
def add_slash(path):
        if path.endswith('/'): return path
        return path + '/'
