'''
Created on 15 avr. 2020

@author: laurentmichel
'''
import os
from lxml import etree
from tests import logger
from copy import deepcopy

class ParameterAppender:
    '''
    classdocs
    '''
    
    def __init__(self, param_template, cab_msd_path, param_path):
        '''
        Constructor
        '''
        self.param_template = param_template

        self.cab_msd_path = cab_msd_path
        logger.info("parse %s", self.cab_msd_path)
        self.cab_msd_tree = etree.parse(self.cab_msd_path)
        
        self.param_path = param_path
        logger.info("parse %s", self.param_path)
        self.param_tree = etree.parse(self.param_path)
        
        self.cab_msd_clean_tree = None
    
    def _get_unique_element(self, elements):
        if len(elements) == 1:
            return elements[0]
        raise Exception("elements must have one item not {}".format(len(elements)))
    
    def _clean_tree(self):
        tree_string = etree.tostring(
            self.cab_msd_tree)
                        
        parser = etree.XMLParser(remove_blank_text=True)
        self.cab_msd_clean_tree = etree.fromstring (tree_string, parser)
        
    def _get_global_instance(self, globals_id):
        return self._get_unique_element(
            self.cab_msd_tree.xpath("/VODML/GLOBALS/INSTANCE[@ID='" + globals_id + "']")
            )
         
    def add_globals(self):
        cab_msd_globals = self.cab_msd_tree.xpath('/VODML/GLOBALS') 
        param_globals = self.param_tree.xpath('/VODML/GLOBALS') 
        for global_ele in self._get_unique_element(param_globals).getchildren():
            logger.info("Adding globals ID=%s", global_ele.attrib["ID"])
            self._get_unique_element(cab_msd_globals).append( global_ele )
            
    def add_param_parameter(self):
        parameters_block = self._get_unique_element(
            self.cab_msd_tree.xpath("//COMPOSITION[@dmrole='cab_msd:Source.parameters']")
            )
        param_block = self._get_unique_element(
            self.param_tree.xpath("//INSTANCE[@dmrole='root']")
            )
        
        logger.info("Adding parameter dmtype=%s", param_block.attrib["dmtype"])
        new_param = etree.fromstring(
            self.param_template
            )
        param_block = deepcopy(param_block)
        param_block.attrib["dmrole"] = "cab_msd:Parameter.measure"
        new_param.append(param_block)
        parameters_block.append(new_param)

    def set_ref(self, host_role, value_role,value_ref):
        block = self._get_unique_element(
            self.cab_msd_tree.xpath("//INSTANCE[@dmrole='" + host_role + "']")
            )
        value_block = self._get_unique_element(
                block.xpath(".//VALUE[@dmrole='" + value_role + "']")
                )
        
        logger.info("Set ref of %s[%s] = %s",host_role, value_role, value_ref)

        value_block.attrib["ref"] = value_ref
        
        if "value" in value_block.attrib.keys():
            value_block.attrib.pop("value")

    def set_value(self, host_role, value_role, value_value):
        block = self._get_unique_element(
            self.cab_msd_tree.xpath("//INSTANCE[@dmrole='" + host_role + "']")
            )
        if "ref" in block.attrib.keys():
            block = self._get_global_instance(block.attrib["ref"])

        value_block = self._get_unique_element(
                block.xpath(".//VALUE[@dmrole='" + value_role + "']")
                )
        logger.info("Set value of %s[%s] = %s",host_role, value_role, value_value)
        value_block.attrib["value"] = value_value
        if "ref" in value_block.attrib.keys():
            value_block.attrib.pop("ref")

    def set_notset_value(self):
        notset_values = self.cab_msd_tree.xpath("//VALUE[@ref='@@@@@@']")
        for notset_value  in notset_values:        
            logger.info("Set value of tag %s as NotSet", notset_value.attrib["dmrole"])
            notset_value.attrib["value"] = "NoSet"
            if "ref" in notset_value.attrib.keys():        
                notset_value.attrib.pop("ref")    
                
    def print(self):
        print(self.tostring() )
        
    def save(self, filepath):
        self._clean_tree()
        with open(filepath, 'wb') as output_file:
            etree.ElementTree(self.cab_msd_clean_tree).write(
                output_file, 
                encoding="utf-8", 
                xml_declaration=False, 
                pretty_print=True)

    def tostring(self):
        self._clean_tree()
        return (etree.tostring(
            self.cab_msd_clean_tree,
            pretty_print=True)).decode("utf-8")    
