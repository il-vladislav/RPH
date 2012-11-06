import os
import io
class Corpus:
        def __init__(self,path_to_dir=None):
                self.path_to_dir = path_to_dir if path_to_dir else []
                

        def emails_as_string(self):
                for file_name in os.listdir(self.path_to_dir):
                        if not file_name.startswith("!"):
                                with io.open(self.add_slash(self.path_to_dir)+file_name,'r', encoding ='utf-8') as body:
                                        yield[file_name,body.read()]                        
                                        
        def add_slash(self, path):
                if path.endswith("/"): return path
                return path + "/"
