'''
Created on 31 mars 2020

@author: laurentmichel
'''
import json
from astropy.io.votable import parse
from client.inst_builder.column_mapping import ColumnMapping
from client.inst_builder.table_iterator import TableIterator
from client.inst_builder.row_filter import RowFilter
from client.inst_builder import logger
from client.launchers.instance_from_votable import InstanceFromVotable
from client.inst_builder.json_mapping_builder import JsonMappingBuilder
from client.inst_builder.instancier import Instancier
from client.inst_builder.json_block_extractor import JsonBlockExtractor
from copy import deepcopy
from utils.dict_utils import DictUtils
from astropy.table._column_mixins import sys

class VodmlInstance(object):
    '''
    classdocs
    '''
    def __init__(self, votable_path):
        #
        # One instancier per TEMPLATES
        # table name taken as keys
        #
        self.instanciers = {}
        self.votable_path = votable_path   
        #
        # Dict translation of the <VODML> block
        #
        self.json_view = {}       
        # Convert the XML mapping block in a dictionary
        self.build_json_view()
        # Make the dictionary  compliant with JSON mapping syntax
        self.build_json_mapping()
        # Build the instancier
        self.build_instancier_map()

        
    def build_json_view(self):
        logger.info("Extracting the VODML block")
        instanceFromVotable = InstanceFromVotable(self.votable_path)
        instanceFromVotable._extract_vodml_block()
        logger.info("Validating the VODML block")
        instanceFromVotable._validate_vodml_block()
        logger.info("Extracting the raw JSON block")        
        self.json_view = instanceFromVotable.json_block     

    def build_json_mapping(self):
        logger.info("Formating the JSON view")
        builder = JsonMappingBuilder(json_dict=self.json_view )
        builder.revert_compositions("COMPOSITION")
        builder.revert_templates()
        builder.revert_elements("INSTANCE")
        builder.revert_elements("VALUE")
        self.json_view = builder.json

    def build_instancier_map(self):
        logger.info("Looking for tables matching TEMPLATES ")
        votable = parse(self.votable_path)
        for template_key in self.json_view["VODML"]["TEMPLATES"].keys():
            logger.info("Looking for a table matching TEMPLATES %s", template_key)

            name = None
            parsed_table = None
            for table in votable.iter_tables():
                if  template_key == table.ID:
                    logger.info("Table with ID = %s found", template_key)
                    name = table.ID
                    parsed_table = table
                    break
            if name == None:
                for table in votable.iter_tables():
                    if  template_key == table.name:
                        logger.info("Table with name = %s found", template_key)
                        name = table.name
                        parsed_table = table
                        break
            if name == None:
                raise Exception("Cannot find table with name or ID = " + name)
            else:
                logger.info("Add Instancier for table %s", name)
                self.instanciers[template_key] = Instancier(
                    template_key,
                    self.votable_path, 
                    parsed_table=parsed_table,
                    json_inst_dict=self.json_view)

    def populate_templates(self, resolve_refs=False):
        for k,v in self.instanciers.items():
            logger.info("populate template %s", k)
            v.resolve_refs_and_values(resolve_refs=resolve_refs)
            v.map_columns()        
 
    def connect_join_iterators(self):
        parse_tables = {}
        for template,instancier in self.instanciers.items():
            parse_tables[template] = instancier.parsed_table
            
        for template,instancier in self.instanciers.items():
            for target,join_iterator in instancier.join_iterators.items():
                logger.info("join template %s with template %s", template, target)
                join_iterator.connect_votable(parse_tables[target])

    
    
 