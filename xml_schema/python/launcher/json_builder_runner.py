'''
Created on 26 mars 2020

@author: laurentmichel
'''
import json
from inst_builder.builder import Builder

class JsonBuilderRunner:
    def __init__(self, sample_file):
        self.sample_file = sample_file
        self.builder = Builder(json_path=sample_file)

    def run(self):
        self.builder.revert_sets("GLOBALS",
                                         default_key='globals')
        #self.builder.revert_compositions("COMPOSITION")
        self.builder.revert_sets("TEMPLATES",
                                         default_key='root')
        self.builder.revert_array()
        self.builder.revert_compositions("COMPOSITION")
        #self.builder.revert_elements("INSTANCE")
        self.builder.revert_elements("VALUE")
        self.builder.revert_elements("MODEL")
    
    def save(self):
        self.builder.save_instance()

    def print(self):
        print(json.dumps(self.builder.json
                         , indent=2
                         , sort_keys=True))


