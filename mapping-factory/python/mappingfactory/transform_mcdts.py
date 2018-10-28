#!/usr/bin/env python
# -*- coding: utf-8 -*-

from transform import  *


            
def main():
    mapping_generator = MappingGenerator()
    mapping_generator.with_types = True
    mapping_generator.parse_vodml_file(filename="../../models/TimeSeries-0.1.vo-dml.xml", model='ts')
    mapping_generator.resolve_inheritance();
    mapping_generator.resolve_constaints();
    #root_object_id = 'cube:DataProduct'
    mapping_generator.root_object_id = 'ts:SimpleTimeSeries'
    
    #cube:NDPoint ts:Record
    #cube:NDPoint.observable cube:MeasurementAxis
    mapping_generator.concrete_classes = { 
                                         "cube:MeasurementAxis.measure": "ts:PhotometricMeasure"
                                         #"cube:MeasurementAxis.measure": ["ts:PhotometricMeasure", "meas:StdTimeMeasure"]
                                         # , "ts:PhotometricCoord": ["ts:Magnitude", "meas:Time"]
                                          }
                                          
 
    #mapping_generator.concrete_classes = {}
    mapping_generator.concrete_types={"meas:CoordMeasure.coord": "ts:Magnitude"}
    mapping_generator.generate_mapping()
    
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
               
    for ac in mapping_generator.mapped_abstract_types :
        print("Abstract type mapped " + ac)
        for sc in mapping_generator.get_sub_types(ac):
            print("   " + sc)
    #parse_vodml_file(filename="/home/michel/workspace/vo-datamodels/provenance/vo-dml/xml/ProvenanceDM.vo-dml.xml", model='provenance')
    #root_object_id = 'provenance:provenance.Entity'
    #print(root_object_id)
    #generate_mapping()

    root = etree.fromstring(mapping_generator.xml_string + "\n")
    print((etree.tostring(root, pretty_print=True)).decode("utf-8") )
if __name__ == "__main__":
    main()   
    sys.exit()   
        
    