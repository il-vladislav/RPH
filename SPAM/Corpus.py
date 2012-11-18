import os
import io
import methods
class Corpus:
        def __init__(self,path_to_dir=None):
                self.path_to_dir = path_to_dir if path_to_dir else []
                

        def emails_as_string(self):
                for file_name in os.listdir(self.path_to_dir):
                        if not file_name.startswith("!"):                                
                                with io.open(methods.add_slash(self.path_to_dir)+file_name,'r', encoding ='utf-8', errors='ignore') as body:
                                        yield[file_name,body.read()]
                        
