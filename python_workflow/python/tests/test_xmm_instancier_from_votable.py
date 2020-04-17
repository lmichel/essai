'''
Created on 31 mars 2020

@author: laurentmichel
'''

import os, json
from utils.json_encoder import MyEncoder
from client.launchers.instance_from_votable import InstanceFromVotable
from tests import data_dir

if __name__ == '__main__':
    base_path = os.path.dirname(os.path.realpath(__file__)) 
    votable_path = os.path.join(data_dir, 
                                "annotated_data",
                                "xmm_detections.annot.xml"
                                )
    instance_from_votable = InstanceFromVotable(votable_path)
    instance = instance_from_votable.build_instance()
    
    print(instance.get_flatten_data_head())
    print(instance.get_data_subset_keys())

    while True:
        inst = instance._get_next_flatten_row()
        if inst != None:
            print(inst)
            print(json.dumps(inst, 
                             indent=2,
                             sort_keys=True,
                             cls=MyEncoder))
            break
        else:
            break



    
    


    
    
    
