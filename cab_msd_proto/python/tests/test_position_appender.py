'''
Created on 15 avr. 2020

@author: laurentmichel
'''
import os
from mapper.position_appender import PositionAppender  
        
if __name__ == '__main__':
        base_path = os.path.dirname(os.path.realpath(__file__)) 

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
        position_appender = PositionAppender(cab_msd_path, position_path)
        position_appender.set_refs("_s_ra", "_s_ra", "_radec_err", "_iauname")
        position_appender.print()
        
 