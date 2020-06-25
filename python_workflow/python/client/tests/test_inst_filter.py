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
        votable_path = os.path.join(data_path, "./data/test_filter.xml")
        json_ref_path = os.path.join(data_path, "./data/test_filter_1.json")
        instancier = Instancier(votable_path
                             , json_inst_dict=DictUtils.read_dict_from_file(json_ref_path))
        instancier.resolve_refs_and_values()

        self.assertListEqual([*instancier.table_iterators], ['meas:Error.statError'], "")
        self.assertDictEqual(instancier.table_iterators['meas:Error.statError'].column_mapping.column_refs
                             , {'_poserr_148': {'parent_role': 'meas:Symmetrical.radius', 'role': 'ivoa:RealQuantity.value', 'index': None, 'field': None}}
                             , "")
        
        instancier.map_columns()
        self.assertDictEqual(instancier.table_iterators['meas:Error.statError'].column_mapping.column_ids
                             , {0: {'name': 'oidsaada', 'ref': None, 'id': '_poserr_148'}}
                             , "")
        self.assertEqual(instancier.table_iterators['meas:Error.statError'].row_filter.__repr__()
                             , "Row filter NoName key=_poserr_148 value=2 col=-1"
                             , "")

        self.assertListEqual(instancier.get_flatten_data_head()
                         ,['meas:Symmetrical.radius(ivoa:RealQuantity.value) [col#0 _poserr_148]'],""),
        cpt=2
        while True:
            inst = instancier._get_next_flatten_row()
            if inst != None:
                self.assertListEqual(inst, [cpt], "")
                cpt += 1
            else:
                break
        instancier.rewind()
        cpt=2
        while True:
            inst = instancier._get_next_row_instance()
            if inst != None:
                self.assertDictEqual(inst
                                     , {
                                      "@dmtype": "meas:Symmetrical",
                                      "meas:Symmetrical.radius": {
                                        "@dmtype": "ivoa:RealQuantity",
                                        "ivoa:Quantity.unit": {
                                          "@dmtype": "ivoa:Unit",
                                          "@value": "arcsec"
                                        },
                                        "ivoa:RealQuantity.value": {
                                          "@dmtype": "ivoa:real",
                                          "@ref": "_poserr_148",
                                          "@value": cpt
                                        }
                                      }
                                    }
                                     , "")
                cpt+=1
            else:
                break

        

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()