import random
import methods
class TrainingCorpus:
        def __init__(self, path):
                self.path = path if path else []
                

        def get_class(self,email_name):
                return 'OK'

        def is_ham(self,email_name):
                my_dict = methods.read_classification_from_file(self.path,'!truth.txt')
                if (my_dict[email_name] == 'HAM'):
                        return True
                return False

        def is_spam(self,email_name):
                my_dict = methods.read_classification_from_file(self.path,'!truth.txt')
                if (my_dict[email_name] == 'SPAM'):
                        return True
                return False
                
        def spams_as_string():
                for file_name in os.listdir(self.path_to_dir):
                        if not file_name.startswith("!"):
                                if (is_spam(file_name)):
                                        with io.open(methods.add_slash(self.path_to_dir)+file_name,'r', encoding ='utf-8') as body:
                                                yield[file_name,body.read()]

        def hams_as_string():
                for file_name in os.listdir(self.path_to_dir):
                        if not file_name.startswith("!"):
                                if (is_ham(file_name)):
                                        with io.open(methods.add_slash(self.path_to_dir)+file_name,'r', encoding ='utf-8') as body:
                                                yield[file_name,body.read()]
                
