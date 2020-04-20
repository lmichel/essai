'''
Created on 31 mars 2020

@author: laurentmichel
'''

import os, json
from utils.json_encoder import MyEncoder
from launcher.instance_builder_runner import InstanceBuilderRunner

if __name__ == '__main__':
    base_path = os.path.dirname(os.path.realpath(__file__)) 
    votable_path = os.path.join(base_path, 
                                "../../mapping_sample", 
                                "sample_gfa.vot")
    json_inst_path = votable_path.replace('.vot', 
                                          '.inst.json')
    instance_builder_runner = InstanceBuilderRunner(
        votable_path,
        json_inst_path)
    instance_builder_runner.run()
    
    instance = instance_builder_runner.instance
    while True:
        inst = instance._get_next_flatten_row(data_subset='BP_band')
        if inst != None:
            print(json.dumps(inst, 
                             indent=2,
                             sort_keys=True,
                             cls=MyEncoder))
        else:
            break
    print(instance.get_flatten_data_head())
    print(instance.get_data_subset_keys())
    
    roles_test = ["ds:dataset.Dataset.curation",
                  "ts:PhotometricMeasure.filter"]
    for role in roles_test:
        searched_instances = instance.search_instance_by_role(role)
        print("element matching dmrole={}".format(role))
        for ele in searched_instances:
            print("==============")
            print(json.dumps(ele, 
                         indent=2,
                         sort_keys=True,
                         cls=MyEncoder))
            print("==============")
            
    ids_test = ["filterG", "filterBP", "filterRP"]
    for ID in ids_test:
        print("element matching ID={}".format(ID))
        searched_ids = instance.search_instance_by_id(ID)
        for ele in searched_ids:
            print("==============")
            print(json.dumps(ele, 
                         indent=2,
                         sort_keys=True,
                         cls=MyEncoder))
            print("==============")
            
    
    
    
    
