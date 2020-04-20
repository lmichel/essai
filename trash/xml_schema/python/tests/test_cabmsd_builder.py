'''
Created on 26 mars 2020

@author: laurentmichel
'''
import os
from launcher.json_builder_runner import JsonBuilderRunner

if __name__ == '__main__':
    base_path = os.path.dirname(os.path.realpath(__file__)) 
    sample_file = os.path.join(base_path, "../../mapping_sample", "sample_cabmsd.json")
    json_builder_runner = JsonBuilderRunner(sample_file)
    json_builder_runner.run()
    json_builder_runner.save()
    json_builder_runner.print()


