import os
import sys
import traceback
import json
import xmltodict

from validator.validator import Validator

base_path = os.path.dirname(os.path.realpath(__file__)) 


validator = Validator(os.path.join(base_path
                                   , "../../schema/"
                                   , "vodml_lite.xsd"))


# Open a file
sample_path = os.path.join(base_path
                         , "../../mapping_sample")
dirs = os.listdir( sample_path )

# This would print all the files and directories
for file in dirs:
    if not file.endswith(".xml"):
        continue
    sample_file = os.path.join(sample_path, file)
   
    try:
        validator.validate_file(sample_file)
        print(file + ' is Valid')
        with open(sample_file, 'r') as f:
            my_xml = f.read()     
            with open(sample_file.replace(".xml", ".json"), 'w') as jsonfile:
                jsonfile.write(json.dumps(xmltodict.parse(my_xml)
                                          , indent=2, sort_keys=True))
    except :
        print(file + ' is not Valid')
        traceback.print_exc()    
        sys.exit(1)                                  
    
        