from lxml import etree
import io

class Validator:

    def __init__(self, xsd_path):
        xmlschema_doc = etree.parse(xsd_path)
        self.xmlschema = etree.XMLSchema(xmlschema_doc)

    def validate_file(self, xml_path: str) -> bool:
        xml_doc = etree.parse(xml_path)
        self.xmlschema.assertValid(xml_doc)
        
    def validate_string(self, xml_string: str) -> bool:
        xml_doc = etree.parse(io.StringIO(xml_string))
        self.xmlschema.assertValid(xml_doc)
