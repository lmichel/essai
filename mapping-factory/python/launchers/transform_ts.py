#!/usr/bin/env python
# -*- coding: utf-8 -*-
'''
This tool helps to build a VODML block from a model.
It must be taken as tool providing component rather than like an automatic mapper.
The editor must rearrange the components by hand according the way he/she wants map its VOTable.
The block is monolitic, there is NO GLOBALS or TEMPLATES
Cross referenced instances are duplicated, they must be restored by hand.
ABstract classes or types are tagged with "abstract=true". 
This shouldn't happen with a correct configuration. 
There are dictionaries, which must be set by hand giving for a specific role all 
the concrete types or classes to use

LM 10/2018
'''
from transform import  *
            
def main():
    mapping_generator = MappingGenerator()
    '''
    Set both the VOTable to parse and the model to be implemented by the VOTabble
    '''
    mapping_generator.parse_vodml_file(filename="../models/TimeSeries.vo-dml.xml", model='ts')
    '''
    merge attributes of super classes with those of the  concrete class
    '''
    mapping_generator.resolve_inheritance();
    '''
    Apply subsetting
    '''
    mapping_generator.resolve_constaints();
    '''
    Set the model to used as root class in the VOMDL mapping block
    '''
    mapping_generator.root_object_id = 'ts:SimpleTimeSeries'
    '''
    This map attach roles with concrete classes. 
    When a role is attached with several concrete classes, the INSTANCE is duplicated.
    The editor keep in charge of placing the right instance at the right place in the mappig block
    ''' 
    mapping_generator.concrete_classes = {"cube:DataProduct.coordSys": "coords:AstroCoordSystem",
                        "cube:DataProduct.coordSys": ["coords:domain.space.SpaceFrame"
                                                    , "coords:domain.time.TimeFrame"
                                                    , "coords:GenericCoordFrame"], 
                        "trans:FrameTransform.operation": "trans:TProjection",
                        "trans:FrameTransform.nativeFrame": "coords:domain.time.TimeFrame",
                        "trans:FrameTransform.targetFrame": "coords:domain.space.SpaceFrame",
                        "cube:MeasurementAxis.measure": "meas:StdTimeMeasure",
                        "cube:NDPoint.observable": ["cube:Observable", "cube:Observable", "cube:Observable"],
                        "cube:DataProduct.dataset": "ds:experiment.ObsDataset"
                        }
    '''
    This map attach roles with concrete types. 
    When a role is attached with several concrete types, the INSTANCE is duplicated.
    The editor keep in charge of placing the right type at the right place in the mapping block
    ''' 
    mapping_generator.concrete_types = {"coords:domain.space.SpaceFrame.refPosition": "coords:domain.space.StdRefLocation",
                      "coords:domain.time.TimeFrame.refPosition": "coords:domain.space.StdRefLocation",
                      "coords:domain.time.TimeFrame.refDirection": "coords:domain.space.StdRefLocation",
                      "coords:domain.time.TimeFrame.time0": "coords:domain.time.MJD",
                      "meas:CoordMeasure.coord": "coords:domain.time.JD",
                      "meas:Error1D.statError": "meas:Symmetrical1D",
                      "meas:Error1D.sysError": "meas:Symmetrical1D",
                      "meas:Error1D.ranError": "meas:Symmetrical1D"
                         }

    '''
    Build an in-memory VOMDL block containing all components of the model 
    '''
    mapping_generator.generate_mapping()
    '''
    Report about which concrete classes have been used to override obstract classes 
    '''
    for ac in mapping_generator.mapped_abstract_classes :
        print("Abstract class mapped " + ac)
        for sc in mapping_generator.get_sub_classes(ac):
            abstract = ""
            if  mapping_generator.object_types[sc].abstract == True:
                abstract = "Abstract "
            print("   " + abstract + sc)
            if abstract != "":
                for sc2 in mapping_generator.get_sub_classes(sc):
                    print("       " + sc2)
    '''
    Report about which concrete types have been used to override obstract types 
    '''              
    for ac in mapping_generator.mapped_abstract_types :
        print("Abstract type mapped " + ac)
        for sc in mapping_generator.get_sub_types(ac):
            print("   " + sc)
    '''
    print out the mapping block
    '''
    root = etree.fromstring(mapping_generator.xml_string + "\n")
    print((etree.tostring(root, pretty_print=True)).decode("utf-8") )
 
'''
does the job
'''    
if __name__ == "__main__":
    main()   
    sys.exit()   
        
    