'''
Created on 15 avr. 2020

@author: laurentmichel
'''
import os
from lxml import etree
from tests import logger
from copy import deepcopy
from mapper.constants import PARAM_TEMPLATES
from mapper.parameter_appender import ParameterAppender
class PositionAppender:
    '''
    classdocs
    '''
    
    def __init__(self, cab_msd_path, param_path):
        '''
        Constructor
        '''
        self.cab_msd_path = cab_msd_path        
        self.position_path = param_path
        
        self.appender = ParameterAppender(
            PARAM_TEMPLATES.POSITION,
            self.cab_msd_path,
            self.position_path
            )

        self.appender.add_globals()
        self.appender.add_param_parameter()
        
    def set_refs(self, ra_ref, dec_ref, err_ref, identifier_ref):
        self.appender.set_ref("coords:Point.axis1", 
                              "ivoa:RealQuantity.value", 
                              ra_ref)
        self.appender.set_ref("coords:Point.axis2", 
                              "ivoa:RealQuantity.value", 
                              dec_ref)
        self.appender.set_ref("meas:Error.statError", 
                              "ivoa:RealQuantity.value", 
                              err_ref)
        self.appender.set_ref("root", 
                              "cab_msd:Source.identifier", 
                              identifier_ref)
        self.appender.set_notset_value()

    def print(self):
        self.appender.print()

        
