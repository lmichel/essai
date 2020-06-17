'''
Created on 6 avr. 2020

@author: laurentmichel
'''
import re, os, sys
import xmltodict

from schema.validator.validator import Validator
from client.inst_builder.json_mapping_builder import JsonMappingBuilder
from client.inst_builder.instancier import Instancier
from client.launchers import logger, data_dir

class InstanceFromVotable:
    
    def __init__(self, votable_path):
        self.votable_path = votable_path
        self.vodml_block = None

    def _extract_vodml_block(self):
        logger.info("extract vodml block from %s", self.votable_path)
        with open(self.votable_path) as xml_file:
            self.vodml_block =re.search(r'<VODML>((.|\n)*?)</VODML>', xml_file.read()).group() 
        
        if self.vodml_block is None :
            raise Exception("No vodml block found")
        logger.info("VODML found")
        
    def _validate_vodml_block(self):

        validator = Validator(os.path.join(data_dir
                                   , "schemas"
                                   , "vodml_lite.xsd"))
        validator.validate_string(self.vodml_block)
        logger.info("VODML block is valid")
        self.json_block = xmltodict.parse(self.vodml_block)
        
    def _build_instance(self):

        builder = JsonMappingBuilder(json_dict=self.json_block)

        builder.revert_sets("GLOBALS",
                                         default_key='globals')
        #self.builder.revert_compositions("COMPOSITION")
        builder.revert_sets("TEMPLATES",
                                         default_key='root')
        builder.revert_array()
        builder.revert_compositions("COMPOSITION")
        builder.revert_elements("INSTANCE")
        builder.revert_elements("VALUE")
        builder.revert_elements("MODEL")

        """
        builder.revert_compositions("COMPOSITION")
        builder.revert_compositions("TEMPLATES"
                                      , default_key='root')
        builder.revert_array()
        builder.revert_elements("INSTANCE")
        builder.revert_elements("VALUE")
        builder.revert_elements("MODEL")
        """
        self.json_vodml_block = builder.json
        logger.info("JSON VODML block built")
        

    def _populate_instance(self, resolve_refs=False):
        self._instancier = Instancier(self.votable_path
                                  , json_inst_dict=self.json_vodml_block)
        self._instancier.set_element_values(resolve_refs=resolve_refs)
        self._instancier.set_array_values()
        self._instancier.map_columns()
        logger.info("VODML instance created")

    def build_instance(self, resolve_refs=False):
        logger.info("Build in memory instance")

        self._extract_vodml_block()
        self._validate_vodml_block()
        self._build_instance()
        self._populate_instance(resolve_refs=resolve_refs)
        return self._instancier


    
