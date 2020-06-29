'''
Created on 22 juin 2020

@author: laurentmichel
'''
import unittest
import os
from client.inst_builder.json_mapping_builder import JsonMappingBuilder
from client.launchers.instance_from_votable import InstanceFromVotable
from tests import logger
from utils.dict_utils import DictUtils

class TestInstance(unittest.TestCase):


    def test_1(self):
        data_path = os.path.dirname(os.path.realpath(__file__))
        votable_path = os.path.join(data_path, "./data/test_join.xml")
        json_ref_path = os.path.join(data_path, "./data/test_join_1.json")
        logger.info("extract vodml block from %s", votable_path)
        instanceFromVotable = InstanceFromVotable(votable_path)
        
        instanceFromVotable._extract_vodml_block()
        instanceFromVotable._validate_vodml_block()
        
        builder = JsonMappingBuilder(json_dict=instanceFromVotable.json_block)
        builder.revert_compositions("COMPOSITION")
        builder.revert_templates()

        builder.revert_elements("INSTANCE")
        builder.revert_elements("VALUE")
        
        #print(DictUtils.get_pretty_json(builder.json))
        self.assertDictEqual(builder.json["VODML"]
                             , DictUtils.read_dict_from_file(json_ref_path)["VODML"]
                             , "=======")


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()