'''
Created on 15 avr. 2020

@author: laurentmichel
'''
import os
from mapper.position_appender import PositionAppender  
from mapper.votable_merger import VOTableMerger
        
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
        position_appender = PositionAppender(cab_msd_path, position_path)
        position_appender.set_identifier("namesaada")
        position_appender.set_position("_ra_146", "_dec_147", 
                                       "_poserr_148", "_syserrcc_152",
                                       "deg", "arcsec")
        position_appender.set_spaceframe("ICRS", None)
        position_appender.set_notset_value()

        print(position_appender.tostring())
        position_appender.save(output_mapping_path)
        
        merger = VOTableMerger(raw_votable_path, output_mapping_path, annot_votable_path)
        merger.insert_mapping()
        
        