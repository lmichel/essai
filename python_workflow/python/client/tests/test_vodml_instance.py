'''
Created on 22 juin 2020

@author: laurentmichel
'''
import unittest
import os
from client.inst_builder.vodml_instance import VodmlInstance
from tests import logger
from utils.dict_utils import DictUtils

class TestVodmlInstance(unittest.TestCase):


    def test_1(self):
        
        #print(DictUtils.get_pretty_json(builder.json))
        self.assertDictEqual(self.vodml_instance.json_view
                             , DictUtils.read_dict_from_file(self.json_ref_path)
                             , "=======")
        
    def test_2(self):
        #print(vodml_instance.instanciers.keys())
        self.assertListEqual(list(self.vodml_instance.instanciers.keys()), 
                             ['Results', 'OtherResults'], 
                             "Instancier keys not matching")

    def test_3(self):
        self.vodml_instance.populate_templates()

    def setUp(self):
        self.data_path = os.path.dirname(os.path.realpath(__file__))
        self.votable_path = os.path.join(self.data_path, "./data/test_vodml_instance.xml")
        self.json_ref_path = os.path.join(self.data_path, "./data/test_vodml_instance_1.json")

        logger.info("processing %s", self.votable_path)
        
        self.vodml_instance = VodmlInstance(self.votable_path)

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()