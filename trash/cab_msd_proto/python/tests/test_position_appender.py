'''
Created on 15 avr. 2020

@author: laurentmichel
'''
import os, sys
import json
from mapper.position_appender import PositionAppender  
from mapper.votable_merger import VOTableMerger
from tests import logger        
if __name__ == '__main__':
        base_path = os.path.dirname(os.path.realpath(__file__)) 

        raw_votable_path = os.path.join(
            base_path, 
            "../../", 
            "data/raw_data", 
            "xmm_detections.xml")  
        annot_votable_path = os.path.join(
            base_path, 
            "../../", 
            "data/annotated_data", 
            "xmm_detections.annot.xml")
        cab_msd_path = os.path.join(
            base_path, 
            "../../", 
            "data/mapping_components", 
            "cab_msd.mapping.xml")  
        position_path = os.path.join(
            base_path, 
            "../../", 
            "data/mapping_components", 
            "position.mapping.xml")
        output_mapping_path = os.path.join(
            base_path, 
            "../../", 
            "data/annotated_data", 
            "xmm_detections.mapping.xml")
        
        with open(base_path + '/xmm.config.json') as json_file:
            data = json.load(json_file)
            
        for measure in data["parameters"]:
            if measure["measure"] == "position":
                logger.info("Position found")
                appender = PositionAppender(cab_msd_path, position_path)
                appender.append_measure(measure)
                print(appender.tostring())

                appender.save(output_mapping_path)
        
        