'''
Created on 6 avr. 2020

@author: laurentmichel
'''
import re, os
import json
import xmltodict

from validator.validator import Validator
from inst_builder.builder import Builder
from inst_builder.instancier import Instancier
from tests import logger

class InstanceFromVotable:
    
    def __init__(self, votable_path):
        self.votable_path = votable_path
        self.vodml_block = None

    def _extract_vodml_block(self):
        
        with open(self.votable_path) as xml_file:
            self.vodml_block =re.search(r'<VODML>((.|\n)*?)</VODML>', xml_file.read()).group() 
        
        if self.vodml_block is None :
            raise Exception("No vodml block found")
        logger.info("VODML found")
        
    def _validate_vodml_block(self):
        base_path = os.path.dirname(os.path.realpath(__file__)) 


        validator = Validator(os.path.join(base_path
                                   , "../../schema/"
                                   , "vodml_lite.xsd"))
        validator.validate_string(self.vodml_block)
        logger.info("VODML block is valid")
        self.json_block = xmltodict.parse(self.vodml_block)
        
    def _build_instance(self):
        builder = Builder(json_dict=self.json_block)

        builder.revert_compositions("COMPOSITION")
        builder.revert_compositions("TEMPLATES"
                                      , default_key='root')
        builder.revert_array()
        builder.revert_elements("INSTANCE")
        builder.revert_elements("VALUE")
        builder.revert_elements("MODEL")
        self.json_vodml_block = builder.json
        logger.info("JSON VODML block built")
        

    def _populate_instance(self):
        self._instancier = Instancier(self.votable_path
                                  , json_inst_dict=self.json_vodml_block)
        self._instancier.set_element_values()
        self._instancier.set_array_values()
        self._instancier.map_columns()
        logger.info("VODML instance created")

    def build_instance(self):
        self._extract_vodml_block()
        self._validate_vodml_block()
        self._build_instance()
        self._populate_instance()
        return self._instancier


    
