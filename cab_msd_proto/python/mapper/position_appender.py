'''
Created on 15 avr. 2020

@author: laurentmichel
'''

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
        
    def set_spaceframe(self, frame, equinox):   
        self.appender.set_value("coords:PhysicalCoordSys.frame" ,
                                "coords:SpaceFrame.spaceRefFrame",
                                frame);
        if equinox is not None:
            self.appender.set_value(
                "coords:PhysicalCoordSys.frame" ,
                "coords:SpaceFrame.equinox",
                equinox);
    
    def set_position(self, ra_ref, dec_ref, err_ref, sys_err_ref, pos_unit, err_unit):
        self.appender.set_ref("cab_msd:STCSphericalPoint.longitude", 
                              "ivoa:RealQuantity.value", 
                              ra_ref)
        self.appender.set_value("cab_msd:STCSphericalPoint.longitude", 
                              "ivoa:Quantity.unit", 
                              pos_unit)
                
        self.appender.set_ref("cab_msd:STCSphericalPoint.latitude", 
                              "ivoa:RealQuantity.value", 
                              dec_ref)
        self.appender.set_value("cab_msd:STCSphericalPoint.latitude", 
                              "ivoa:Quantity.unit", 
                              pos_unit)
                    
        if err_ref is not None:
            self.appender.set_ref("meas:Error.statError", 
                                  "ivoa:RealQuantity.value", 
                                  err_ref)
        
            self.appender.set_value("meas:Error.statError", 
                                    "ivoa:Quantity.unit", 
                                    err_unit)
        if sys_err_ref is not None:
            self.appender.set_ref("meas:Error.sysError", 
                                  "ivoa:RealQuantity.value", 
                                  sys_err_ref)
        
            self.appender.set_value("meas:Error.sysError", 
                                   "ivoa:Quantity.unit", 
                                err_unit)
            
    def set_identifier(self, identifier_ref):
        self.appender.set_ref("root", 
                              "cab_msd:Source.identifier", 
                              identifier_ref)

    def set_notset_value(self):
        self.appender.set_notset_value()
        
    def tostring(self):
        return self.appender.tostring()
        
    def save(self, output_path):
        self.appender.save(output_path)

        
