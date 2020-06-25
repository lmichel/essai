'''
Created on 22 juin 2020

@author: laurentmichel
'''
import unittest
import os
from client.inst_builder.instancier import Instancier
from utils.dict_utils import DictUtils

class TestInstance(unittest.TestCase):


    def test_1(self):
        self.maxDiff = None
        data_path = os.path.dirname(os.path.realpath(__file__))
        votable_path = os.path.join(data_path, "./data/test_globals.xml")
        json_ref_path = os.path.join(data_path, "./data/test_globals_1.json")
        instancier = Instancier(votable_path
                             , json_inst_dict=DictUtils.read_dict_from_file(json_ref_path))
        instancier.resolve_refs_and_values(resolve_refs=True)

        #print(DictUtils.get_pretty_json(instancier.json["VODML"]["TEMPLATES"]["my:other.role"]))
        self.assertDictEqual(instancier.json["VODML"]["TEMPLATES"]["my:other.role"]
                             ,{
                              "@ID": "TestParamRef",
                              "@dmtype": "Whatever",
                              "coords:whatever": {
                                "@dmtype": "coords:StdRefLocation",
                                "coords:StdRefLocation.position": {
                                  "@dmtype": "ivoa:string",
                                  "@ref": "param_ref",
                                  "@value": "param_value"
                                }
                              }
                            } 
                             , "")
        

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()