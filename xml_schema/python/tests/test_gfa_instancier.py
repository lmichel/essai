'''
Created on 31 mars 2020

@author: laurentmichel
'''

import os, json
from inst_builder.instancier import Instancier
from utils.json_encoder import MyEncoder

if __name__ == '__main__':
    base_path = os.path.dirname(os.path.realpath(__file__)) 
    votable_path = os.path.join(base_path, "../../mapping_sample", "sample_gfa.vot")
    json_inst_path = votable_path.replace('.vot', '.inst.json')
    instancier = Instancier(votable_path, json_inst_path)
    instancier.get_elements()
    instancier.get_array_elements()
    instancier._map_columns()
    #print(json.dumps(instancier.json, indent=2, sort_keys=True))
    #print(instancier.column_refs)  
    ##   instancier._get_row_instance(row)
    #    print(instancier.array_instance)
    #    print(json.dumps(instancier.array_instance, indent=2, sort_keys=True, cls=MyEncoder))
    while True:
        inst = instancier._get_next_flatten_row(data_subset='BP_band')
        if inst != None:
            print(json.dumps(inst, indent=2, sort_keys=True, cls=MyEncoder))
        else:
            break
    print(instancier._get_flatten_data_head())
    print(instancier._get_data_subset_keys())
    
    
    
