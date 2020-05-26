'''
Apply the JSON mapping descriptor for the XMM catalog to the XMM VOTABLE 
Created on 15 avr. 2020

@author: laurentmichel
'''
import os, sys
import json

from tests import data_dir
from tests import logger  

from product_annoter.mapper.position_appender import PositionAppender  
from product_annoter.mapper.status_appender import StatusAppender  
from product_annoter.mapper.votable_merger import VOTableMerger
from product_annoter.mapper.identifier_appender import IdentifierAppender
      
if __name__ == '__main__':
        base_path = os.path.dirname(os.path.realpath(__file__)) 

        raw_votable_path = os.path.join(
            data_dir, 
            "raw_data", 
            "xmm_detections.xml")  
        annot_votable_path = os.path.join(
            data_dir, 
            "annotated_data", 
            "4xmm_detections.annot.xml")
        mango_path = os.path.join(
            data_dir, 
            "mapping_components", 
            "mango.mapping.xml")  
        component_path = os.path.join(
            data_dir, 
            "mapping_components")
        output_mapping_path = os.path.join(
            data_dir, 
            "annotated_data", 
            "4xmm_detections.mapping.xml")
        
        with open(data_dir + '/product_configs/4xmm.mango.config.json') as json_file:
            data = json.load(json_file)
           
        appender = IdentifierAppender(mango_path)
            
        appender.append_measure(data)
        appender.save(output_mapping_path)
 
        for measure in data["parameters"]:
            if measure["measure"] == "LonLatSkyPosition":
                logger.info("Position found")
                appender = PositionAppender(output_mapping_path, component_path)
                appender.append_measure(measure)
                #print(appender.tostring())
                appender.save(output_mapping_path)
            elif measure["measure"] == "status":
                logger.info("Status found")
                appender = StatusAppender(output_mapping_path, component_path)
                appender.append_measure(measure)
                #print(appender.tostring())
                appender.save(output_mapping_path)
        
        merger = VOTableMerger(raw_votable_path, output_mapping_path, annot_votable_path)
        merger.insert_mapping()