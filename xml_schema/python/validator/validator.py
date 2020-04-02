from lxml import etree

class Validator:

    def __init__(self, xsd_path):
        xmlschema_doc = etree.parse(xsd_path)
        self.xmlschema = etree.XMLSchema(xmlschema_doc)

    def validate(self, xml_path: str) -> bool:
        xml_doc = etree.parse(xml_path)
        self.xmlschema.assertValid(xml_doc)
