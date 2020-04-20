import xmlschema

class Validator:

    def __init__(self, xsd_path):
        self.xmlschema = xmlschema.XMLSchema11(xsd_path)


    def validate_file(self, xml_path: str) -> bool:
        self.xmlschema.is_valid(xml_path)
        
    def validate_string(self, xml_string: str) -> bool:
        self.xmlschema.is_valid(xml_string)
