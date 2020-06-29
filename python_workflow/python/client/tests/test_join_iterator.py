'''
Created on 30 juin 2020

@author: laurentmichel
'''
import unittest
import os
from astropy.io.votable import parse
from client.inst_builder.join_iterator import JoinIterator

class Test(unittest.TestCase):


    def testName(self):
        self.assertEqual(self.join_iterator.instancier.get_flatten_data_head()
                         ,['test:detection(test:detection.id) [col#1 _foreign]', 'test:detection(test:detection.num) [col#0 _num_148]'],"")
                                              
    def setUp(self):
        self.data_path = os.path.dirname(os.path.realpath(__file__))
        self.votable_path = os.path.join(self.data_path, "./data/test_vodml_instance.xml")
        votable = parse(self.votable_path)
        for table in votable.iter_tables():
            print(table.name)
            if  "OtherResults" == table.name:
                self.parsed_table = table
                self.join_iterator = JoinIterator(
                    "OtherResults",
                    "_poserr_148",
                    "_foreign",
                    {
                        "@foreign": "_foreign",
                        "@primary": "_poserr_148",
                        "@table_ref": "OtherResults",
                        "test:detection": {
                            "@dmtype": "test:Detection",
                            "test:detection.id": {
                                "@dmtype": "ivoa:real",
                                "@ref": "_foreign",
                                "@value": ""
                                },
                            "test:detection.num": {
                                "@dmtype": "ivoa:real",
                                "@ref": "_num_148",
                                "@value": ""
                                }
                            }
                        }
                    )
                self.join_iterator.connect_votable(self.parsed_table)
                
 





if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()