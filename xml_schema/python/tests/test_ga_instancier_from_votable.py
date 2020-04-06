'''
Created on 31 mars 2020

@author: laurentmichel
'''

import os, json
from utils.json_encoder import MyEncoder
from launcher.instance_from_votable import InstanceFromVotable

if __name__ == '__main__':
    base_path = os.path.dirname(os.path.realpath(__file__)) 
    votable_path = os.path.join(base_path, 
                                "../../mapping_sample",
                                "sample_ga.vot"
                                )
    instance_from_votable = InstanceFromVotable(votable_path)
    instance = instance_from_votable.build_instance()
    while True:
        inst = instance._get_next_row_instance()
        if inst != None:
            print(json.dumps(inst, 
                             indent=2,
                             sort_keys=True,
                             cls=MyEncoder))
        else:
            break


    
    
    
