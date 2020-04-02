'''
Created on 26 mars 2020

@author: laurentmichel
'''
import os, json
from inst_builder.builder import Builder

if __name__ == '__main__':
    base_path = os.path.dirname(os.path.realpath(__file__)) 
    sample_file = os.path.join(base_path, "../../mapping_sample", "sample_gfa.json")
    builder = Builder(sample_file)
    builder.get_compositions("COMPOSITION")
    builder.get_compositions("TEMPLATES", default_key='root')
    builder.get_array()
    builder.get_elements("INSTANCE")
    builder.get_elements("VALUE")
    builder.get_elements("MODEL")
    builder.save_instance()
    print(json.dumps(builder.json , indent=2, sort_keys=True))


