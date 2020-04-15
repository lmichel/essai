'''
Created on 15 avr. 2020

@author: laurentmichel
'''
import os
from lxml import etree
from tests import logger
from copy import deepcopy
from mapper.constants import PARAM_TEMPLATES
class PositionAppender:
    '''
    classdocs
    '''
    
    def __init__(self):
        '''
        Constructor
        '''
        parser = etree.XMLParser()
        self.base_path = os.path.dirname(os.path.realpath(__file__)) 

        self.cab_msd_path = os.path.join(
            self.base_path, 
            "../../", 
            "data/mapping_components", 
            "cab_msd.mapping.xml")
        logger.info("parse %s", self.cab_msd_path)
        self.cab_msd_tree = etree.parse(self.cab_msd_path,parser)
        
        self.position_path = os.path.join(
            self.base_path, 
            "../../", 
            "data/mapping_components", 
            "position.mapping.xml")
        logger.info("parse %s", self.position_path)
        self.position_tree = etree.parse(self.position_path, parser)
    
    def _get_unique_element(self, elements):
        if len(elements) == 1:
            return elements[0]
        raise Exception("elements must have one item not {}".format(len(elements)))
          
    def add_globals(self):
        cab_msd_globals = self.cab_msd_tree.xpath('/VODML/GLOBALS') 
        position_globals = self.position_tree.xpath('/VODML/GLOBALS') 
        for global_ele in self._get_unique_element(position_globals).getchildren():
            logger.info("Adding globals ID=%s", global_ele.attrib["ID"])
            self._get_unique_element(cab_msd_globals).append( global_ele )
            
    def add_position_parameter(self):
        parameters_block = self._get_unique_element(
            self.cab_msd_tree.xpath("//COMPOSITION[@dmrole='cab_msd:Source.parameters']")
            )
        position_block = self._get_unique_element(
            self.position_tree.xpath("//INSTANCE[@dmrole='root']")
            )
        
        logger.info("Adding parameter dmtype=%s", position_block.attrib["dmtype"])
        new_param = etree.fromstring(
            PARAM_TEMPLATES.POSITION
            )
        position_block = deepcopy(position_block)
        position_block.attrib["dmrole"] = "cab_msd:Parameter.measure"
        new_param.append(position_block)
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
        
if __name__ == '__main__':
    position_appender = PositionAppender()
    position_appender.add_globals()
    position_appender.add_position_parameter()
    position_appender.set_ref("coords:Point.axis1", "ivoa:RealQuantity.value", "_s_ra")
    position_appender.set_ref("coords:Point.axis2", "ivoa:RealQuantity.value", "_s_dec")
    position_appender.set_ref("meas:Error.statError", "ivoa:RealQuantity.value", "_radec_err")
    position_appender.set_value("root", "cab_msd:Source.identifier", "IAUNANE")
    position_appender.set_notset_value()

    print((etree.tostring(position_appender.cab_msd_tree
                          , pretty_print=True)).decode("utf-8") )
