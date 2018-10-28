#!/usr/bin/env python
# -*- coding: utf-8 -*-

from lxml import etree
from xml.sax.expatreader import AttributesImpl
import sys, urllib
from setuptools.command.easy_install import sys_executable



print("==============")
class vodmlObjectType:
    def __init__(self, vomdlid, name, superclass, attributes, abstract=False, constraints=dict() ):
        self.vodmlid = vomdlid
        self.name = name
        self.superclass = superclass
        self.attributes = attributes
        self.abstract = abstract
        self.constraints = constraints
        
    def __str__(self):
        retour = ""
        if self.abstract:
            retour += "Abstract "
        retour +=  "OBJECT " 
        if self.superclass != '':
            retour += "(extends " + self.superclass + ") "
        retour += self.name + " " + self.vodmlid +"\nATTRIBUTES\n"
        for attribute in self.attributes.values():
            retour += " - " + attribute.__str__()
        retour += "CONSTRAINTS " + self.constraints.__repr__()
        return retour
    
    def __repr__(self):
        return self.__str__()
                 
class vodmlDataType:
    def __init__(self, vodmlid, name, ref, superclass, attributes, abstract=False,constraints=dict() ):
        self.vodmlid = vodmlid
        self.ref = ref
        self.name = name
        self.superclass = superclass
        self.attributes = attributes
        self.abstract = abstract
        self.constraints = constraints
        
    def __str__(self):
        return self.__repr__()
    def __repr__(self):
        retour = ""
        if self.abstract:
            retour += "Abstract "
        retour +=  "DATATYPE " + self.name + " " + self.vodmlid + " " + self.ref +"\n"
        #for attribute in self.attributes:
        #    retour += attribute.__repr__()
        retour += "\nATTRIBUTES " + self.attributes.__repr__()
        retour += "\nCONSTRAINTS " + self.constraints.__repr__()
        return retour
        
        
class vodmlAttribute:    
    def __init__(self, vomdlid, name, datatype, arraySize):
        self.vodmlid = vomdlid
        self.name = name
        self.datatype = datatype
        self.arraySize = arraySize
    def __str__(self):
        retour =  "ATTRIBUTE "
        retour += "(arraySize " + str(self.arraySize) + ") "
        retour += self.name + " " + self.vodmlid +" "
        retour += self.datatype.__str__()
        return retour
    def __repr__(self):
        return self.__str__()
    
class vodmlConstraint:    
    def __init__(self, name, datatype):
        self.name = name
        self.datatype = datatype
    def __str__(self):
        retour =  "CONSTRAINT "
        retour += self.name + "=>" + self.datatype
        return retour
    def __repr__(self):
        return self.__str__()

class vodmlImport:
    def __init__(self, name, url):
        self.name = name
        self.url = url
    def __str__(self):
        retour =  "IMPORT " + self.name + " " + self.url +"\n"
        return retour
    def __repr__(self):
        retour =  "IMPORT " + self.name + " " + self.url +"\n"
        return retour

class MappingGenerator:
    def __init__(self):
        self.object_types = dict()
        self.data_types = dict()
        self.primitive_types = dict()
        self.imports = dict()
        self.parsed_imports = dict()
        self.concrete_classes = dict()
        self.concrete_types = dict()
        self.mapped_abstract_classes =  []
        self.mapped_abstract_types =  []
        self.xml_string = ""
        self.root_object_id = None

    def read_package(self, modelName, packageNode):
        if packageNode.tag == 'package':
            packageName =  packageNode.find("name").text
            print (">>>>>>>>>> PACKAGE " + packageName)
            for child in packageNode.findall('*'):
                if child.tag == "objectType":
                    ot = self.read_object_type(modelName, child)
                    self.logMsg("ADD OBJECT " , ot.__repr__())
                    self.object_types[ot.vodmlid] = ot
                elif child.tag == 'primitiveType'   or child.tag == 'enumeration':
                    dt = self.read_data_type(modelName, child)
                    self.logMsg("ADD TYPE " , dt.__repr__())
                    self.primitive_types[dt.vodmlid] = dt
                elif child.tag == "dataType" :
                    ot = self.read_data_type(modelName, child)
                    self.logMsg("ADD DATA TYPE " , ot.__repr__())
                    self.data_types[ot.vodmlid] = ot
                elif child.tag == 'package':
                    self.read_package(modelName, child)

    def read_data_type(self, modelName,datatypeTag):
        vodmlid = ''
        name = ''
        superclass = ''
        ref = ''
        attributes = dict()
        constraints = dict()
        abstract = False
        if datatypeTag.get("abstract") == "true":
            abstract = True
        for datatypeTagChild in datatypeTag.findall('*'):
            if datatypeTagChild.tag == "vodml-id":
                vodmlid = modelName + ":" + datatypeTagChild.text
            elif datatypeTagChild.tag == "name":
                name = datatypeTagChild.text
            elif datatypeTagChild.tag == "vodml-ref":
                ref = datatypeTagChild.text
            elif datatypeTagChild.tag == "attribute" or datatypeTagChild.tag == "reference" :
                att = self.read_attribute(modelName,datatypeTagChild)
                attributes[att.vodmlid] = att
            elif datatypeTagChild.tag == "extends":
                for superTypeChild in datatypeTagChild.findall('vodml-ref'):
                    superclass = superTypeChild.text
                    break
            elif datatypeTagChild.tag == "constraint":
                print("TYPE CONST")
                ct = self.read_constraint(modelName,datatypeTagChild)
                constraints[ct.name] = ct
                #sys.exit()
            elif datatypeTagChild.tag == "reference":
                att = self.read_reference(modelName,datatypeTagChild)
                attributes[att.vodmlid] = att
       
        return vodmlDataType(vodmlid, name, ref, superclass, attributes, abstract,constraints)

    def read_attribute(self, modelName,attributeTag):
        dataType = ''
        arraySize = 2
        for attributeTagChild in attributeTag.findall('*'):
            if attributeTagChild.tag == "vodml-id":
                vodmlid = modelName + ":" + attributeTagChild.text
            elif attributeTagChild.tag == "name":
                name = attributeTagChild.text
            elif attributeTagChild.tag == "multiplicity":
                for multiplicityTagChild in attributeTagChild.findall('maxOccurs'):
                    arraySize = int(multiplicityTagChild.text)
            elif attributeTagChild.tag == "datatype" or attributeTagChild.tag == 'reference':
                dataType = self.read_data_type(modelName,attributeTagChild)
        return vodmlAttribute(vodmlid, name, dataType, arraySize)
         
         
    def read_constraint(self, modelName,attributeTag):  
        role=""
        datatype = ""  
        for attributeTagChild in attributeTag.findall('*'):
            if attributeTagChild.tag == "role":
                for attributeTagSubChild in attributeTagChild.findall('*'):
                    if attributeTagSubChild.tag == "vodml-ref":
                        role = attributeTagSubChild.text
            elif attributeTagChild.tag == "datatype" :
                for attributeTagSubChild in attributeTagChild.findall('*'):
                    if attributeTagSubChild.tag == "vodml-ref":
                        datatype = attributeTagSubChild.text
        return vodmlConstraint(role, datatype)
     
    def read_reference(self, modelName,ReferenceTag):
        for ReferenceTagChild in ReferenceTag.findall('*'):
            if ReferenceTagChild.tag == "vodml-id":
                vodmlid = modelName + ":" + ReferenceTagChild.text
            elif ReferenceTagChild.tag == "name":
                name = ReferenceTagChild.text
            elif ReferenceTagChild.tag == "datatype":
                dataType = self.read_data_type(modelName,ReferenceTagChild)
        return vodmlAttribute(vodmlid, name, dataType, 1)
    
    def read_object_type(self, modelName, objectTypeTag):
        attributes = dict()
        constraints = dict()
        superclass = ''
    
        abstract = False
        if objectTypeTag.get("abstract") == "true":
            abstract = True
        for objectTypeChild in objectTypeTag.findall('*'):
            if objectTypeChild.tag == "vodml-id":
                vodmlid = modelName + ":" + objectTypeChild.text
    
            elif objectTypeChild.tag == "name":
                name = objectTypeChild.text
            elif objectTypeChild.tag == "composition":
                #or multiplicityTagChild in objectTypeChild.findall('maxOccurs'):
                #    arraySize = int(multiplicityTagChild.text)
                att = self.read_attribute(modelName,objectTypeChild)
                attributes[att.vodmlid] = att
            elif objectTypeChild.tag == "extends":
                for superTypeChild in objectTypeChild.findall('vodml-ref'):
                    superclass = superTypeChild.text
                    break
            elif objectTypeChild.tag == "attribute":
                att = self.read_attribute(modelName,objectTypeChild)
                attributes[att.vodmlid] = att

            elif objectTypeChild.tag == "reference":
                att = self.read_reference(modelName,objectTypeChild)
                attributes[att.vodmlid] = att

            elif objectTypeChild.tag == "constraint":
                print("OBJ CONST " + name)
                ct = self.read_constraint(modelName,objectTypeChild)
                constraints[ct.name] = ct
                #sys.exit()

        obj = vodmlObjectType(vodmlid, name, superclass, attributes, abstract, constraints)
        if obj.vodmlid == "cube:DataProduct":
            print(obj.__repr__())
            print("=======================")
            #sys.exit()

        return obj
                      
    
    def parse_vodml_file(self, filename='', model=None):
        print( "Reading FILE " + filename + " for model ", model)
    
        if filename.startswith("http://") or filename.startswith("https://") :
            tree = etree.ElementTree(file=urllib.request.urlopen(filename))
        else :
            tree = etree.parse(filename)
        
        root = tree.getroot()
        ns = {"vo-dml": "http://www.ivoa.net/xml/VODML/v1"}
        if model == None:
            modelName =  root.find("name").text
        else :
            modelName = model
    
        print( "PARSE FILE " + filename + " for model ", model)
            
        for node in tree.xpath("import"):
            name = node.find('name').text
            url = node.find('url').text
            if not name in self.imports:
                # Let's take the import name as VODML prefix
                modelName = name
                print ("PARSE IMPORT " + name)
                self.parse_vodml_file(filename=url, model=modelName)
                print ("END PARSE " + name)   
                self.imports[name]=url
    
        if model == None:
            modelName =  root.find("name").text
        else :
            modelName = model
    
        for node in tree.xpath("*"):
            if node.tag == 'package':
                self.read_package(modelName, node)
            elif node.tag == 'primitiveType'  or node.tag == 'enumeration':
                dt = self.read_data_type(modelName, node)
                self.logMsg("ADD primitiveType " , dt.vodmlid)
                self.primitive_types[dt.vodmlid] = dt
            elif node.tag == 'dataType' or node.tag == 'reference':
                dt = self.read_data_type(modelName, node)
                self.logMsg("ADD dataType " , dt.__repr__())
                self.data_types[dt.vodmlid] = dt
            elif node.tag == 'objectType':
                dt = self.read_object_type(modelName, node)
                self.logMsg("ADD objectType " , dt.vodmlid)
                self.object_types[dt.vodmlid] = dt
    
    def generate_single_attribute_mapping(self, attribute):
        print(" generate_single_attribute_mapping  " + attribute.__repr__())
        print(" generate_single_attribute_mapping  " + attribute.__class__.__name__)
        print("===============")
        ref = attribute.datatype.ref
        ars = ""
        type = " dmtype='" +  attribute.datatype.vodmlid + "' "
        if attribute.arraySize != 1:
            ars = "arraySize=" +  str(attribute.arraySize) 
        if ref in self.primitive_types:
            self.append_xml( "<VALUE dmrole='" + attribute.vodmlid + "'  " +  ars + " ref='@@@@@@'></VALUE>")
        elif ref in self.data_types :
            dt = self.data_types[ref]
            self.generate_data_mapping(dt, attribute.vodmlid, attribute.arraySize)
        elif ref in self.object_types :
            self.generate_object_mapping(self.object_types[ref], attribute.vodmlid, attribute.arraySize)
        else:
            print("REF NOT FOUND " + attribute.__class__.__name__)
            print("REF NOT FOUND " + attribute.__repr__())
            print("===============")
            sys.exit()
            
    def generate_attribute_mapping(self, attribute):
 
        if attribute.arraySize == 0 :
            return
        elif attribute.arraySize == 1:
            self.generate_single_attribute_mapping(attribute)
        elif attribute.arraySize == -1:
            self.append_xml( "<COLLECTION  size='-1'>")
            self.generate_single_attribute_mapping(attribute)
            self.append_xml( "</COLLECTION>")
        else:
            self.append_xml( "<COLLECTION  size='" + str(attribute.arraySize) + "'>")
            #for num in range(0, attribute.arraySize):
            #    print("======== add " + attribute.__repr__())
            self.generate_single_attribute_mapping(attribute)
            self.append_xml( "</COLLECTION>")
       
     
        
    def generate_data_mapping(self, objectType, attrId, arraySize):
        am = ""
        sc_flag = ""
        dmtype= ""
        if objectType.abstract:
            self.mapped_abstract_types.append(objectType.vodmlid)
            am =  " abstract='true' "
            dmtype = " dmtype='" + objectType.vodmlid + "'"
    
            if attrId in self.concrete_types.keys():
                #print("take " +  concrete_types[attrId] + " instead of " + objectType.vodmlid)
                objectType = self.data_types[self.concrete_types[attrId]]
                sc_flag = "(SubT)"
                dmtype = " dmtype='" + sc_flag + objectType.vodmlid + "'"
    
                if objectType.abstract == False:
                    am = ""
                else:
                    self.mapped_abstract_types.append(objectType.vodmlid)
        self.append_xml( "<INSTANCE dmrole='" + attrId + "' " + dmtype + " " + am + ">")
        for attribute in objectType.attributes.values():
            self.generate_attribute_mapping(attribute)
        self.append_xml( "</INSTANCE>")
    
    def get_single_oject_mapping(self, objectType, dmtype, qualifiers):
        str = "<INSTANCE dmrole='" + dmtype + "' " + qualifiers + ">"
        self.append_xml(str)
        read_attributes = []
        for attribute in objectType.attributes.values():
            if attribute.vodmlid not in read_attributes:
                #if attribute.vodmlid in objectType.constraints:
                #    print("====== FUND " + attribute.__repr__() )
                self.generate_attribute_mapping(attribute)
                read_attributes.append(attribute.vodmlid)
            else:
                print( "SKIPPP " + attribute.vodmlid )
        self.append_xml( "</INSTANCE>")
            
    def generate_object_mapping(self, objectType, attrId, arraySize):
        am = ""
        sc_flag = "";
        type = ""
        if attrId in self.concrete_classes.keys():
            if  isinstance(self.concrete_classes[attrId], list):
                for classId in self.concrete_classes[attrId]:
                    sc_flag = "(TSubC " + objectType.vodmlid + ")"
                    objectType = self.object_types[classId]
                    type = " dmtype='" + sc_flag + objectType.vodmlid + "'"
                    self.get_single_oject_mapping(objectType,attrId,  type + " " + am)   
                    if objectType.abstract == True:
                        self.mapped_abstract_classes.append(objectType.vodmlid)
            else:
                objectType = self.object_types[self.concrete_classes[attrId]]
                sc_flag = "(SubC)"
                type = " dmtype='" + sc_flag + objectType.vodmlid + "'"
                if objectType.abstract == False:
                    am = ""
                    #type = ""
                else:
                    self.mapped_abstract_classes.append(objectType.vodmlid)
                self.get_single_oject_mapping(objectType,attrId,  type + " " + am)     

#         elif objectType.abstract:
#             self.mapped_abstract_classes.append(objectType.vodmlid)
#             am =  " abstract='true' "
#             type = "dmtype='" + objectType.vodmlid + "'"
#             if attrId in self.concrete_classes.keys():
#                 objectType = self.object_types[self.concrete_classes[attrId]]
#                 sc_flag = "(SubC)"
#                 type = " dmtype='" + sc_flag + objectType.vodmlid + "'"
#                 if objectType.abstract == False:
#                     am = ""
#                     type = ""
#                 else:
#                     self.mapped_abstract_classes.append(objectType.vodmlid)
#             self.get_single_oject_mapping(objectType,attrId,  type + " " + am)     
        else :
            type = " dmtype='"  + objectType.vodmlid + "'"
            self.get_single_oject_mapping(objectType,attrId,  type + " " + am)     
    
    def get_sub_classes(self, superClassId):
        retour = [];
        for k, v in self.object_types.items():
            if v.superclass == superClassId:
                retour.append(k)
        return retour;
    
    def get_sub_types(self, superClassId):
        retour = [];
        for k, v in self.data_types.items():
            if v.superclass == superClassId:
                retour.append(k)
        return retour;
    
    def append_xml(self, string):
        self.xml_string +=  string  
        return
    
    def logMsg(self, label, msg):
        #if( msg.endswith("SpatialCoord")):
         print(label + " " + msg)
    
    
    def resolve_constaints(self):
         for obj in  self.object_types.values():
            for attribute in obj.attributes.values():
                if attribute.vodmlid in obj.constraints.keys():
                    new_type = obj.constraints[attribute.vodmlid].datatype
                    if new_type in self.data_types.keys():
                        #attribute.datatype = self.data_types[new_type]
                        print(" @@@@1 " + attribute.datatype.__repr__() + "<<" + self.data_types[new_type].__repr__())
                    elif new_type in self.object_types.keys():
                        #attribute.datatype = self.object_types[new_type]
                        print(" @@@@2 " + attribute.datatype.__repr__() + "<<" + self.object_types[new_type].__repr__())
                    print("====== FUND " + obj.vodmlid + " " + attribute.vodmlid + "==>" + new_type)
                    
                
    def resolve_inheritance(self):
        for obj in  self.data_types.values():
            superclass = obj.superclass
            while superclass != '':
                sup_obj= None
                if superclass in self.data_types :
                    sup_obj = self.data_types[superclass]
                else:
                    print( superclass + " Not in dataType")
                    sys.exit(1)
                sup_obj = self.data_types[superclass]
                for k,v in sup_obj.attributes.items():
                    obj.attributes[k] = v
                for k,v in sup_obj.constraints.items():
                    obj.constraints[k] = v
                superclass = sup_obj.superclass
#             if obj.vodmlid == "coords:domain.time.JD":
#                 print(obj.__repr__())
#                 print("=======================")
#                 sys.exit()


        for obj in  self.object_types.values():
            superclass = obj.superclass
            if obj.vodmlid == "meas:StdTimeMeasure":
                print("=======================")
                print(obj.__repr__())
            while superclass != '':
                sup_obj= None            
                if superclass in self.object_types :
                    sup_obj = self.object_types[superclass]
                elif superclass in self.data_types :
                    sup_obj = self.data_types[superclass]
                else:
                    print( superclass + " Neither inndataType or objectType")
                    sys.exit(1)
                print(" merge " + sup_obj.vodmlid)

                for k,v in sup_obj.attributes.items():
                    print(" add " + k + " "  + v.__repr__())
                    obj.attributes[k] = v
                for k,v in sup_obj.constraints.items():
                    obj.constraints[k] = v
                superclass = sup_obj.superclass
            if obj.vodmlid == "meas:StdTimeMeasure":
                print(obj.__repr__())
                print("=======================")
                #sys.exit()
           
    def generate_mapping(self):
        print(self.root_object_id)
        rootObjectType = self.object_types[self.root_object_id]
        self.append_xml("<VODML>")
        self.append_xml("<MODELS>")
        for k,v in self.imports.items():
            self.append_xml("<MODEL>")
            self.append_xml("<NAME>" + k + "</NAME>")
            self.append_xml("<URL>" + v + "</URL>")
            self.append_xml("</MODEL>")
        self.append_xml("</MODELS>")
        self.append_xml("<GLOBALS>")
        self.append_xml("</GLOBALS>")
        self.append_xml("<TEMPLATES>")
        self.generate_object_mapping(rootObjectType, 'root', 1)
        self.append_xml("</TEMPLATES>")
        self.append_xml("</VODML>")
            
def main():
    mapping_generator = MappingGenerator()
    mapping_generator.parse_vodml_file(filename="../../models/Cube-1.0.vo-dml.xml", model='cube')
    mapping_generator.resolve_inheritance();
    mapping_generator.resolve_constaints();
    #root_object_id = 'cube:DataProduct'
    mapping_generator.root_object_id = 'cube:SparseCube'
    
    mapping_generator.concrete_classes = {"cube:DataProduct.coordSys": "coords:AstroCoordSystem",
                        "coords:AstroCoordSystem.coordFrame": ["coords:domain.space.SpaceFrame"
                                                             , "coords:domain.time.TimeFrame"
                                                             , "coords:GenericCoordFrame"], 
                        "trans:FrameTransform.operation": "trans:TProjection",
                        "trans:FrameTransform.nativeFrame": "coords:domain.time.TimeFrame",
                        "trans:FrameTransform.targetFrame": "coords:domain.space.SpaceFrame",
                        "cube:MeasurementAxis.measure": "meas:StdTimeMeasure",
                        #"cube:NDPoint.observable": ["meas:TimeMeasure"
                        #                            , "meas:GenericCoordMeasure"
                        #                            ],
                        }
    
    mapping_generator.concrete_types = {"coords:domain.space.SpaceFrame.refPosition": "coords:domain.space.StdRefLocation",
                      "coords:domain.time.TimeFrame.refPosition": "coords:domain.space.StdRefLocation",
                      "coords:domain.time.TimeFrame.refDirection": "coords:domain.space.StdRefLocation",
                      "coords:domain.time.TimeFrame.time0": "coords:domain.time.MJD",
                      "meas:CoordMeasure.coord": "coords:domain.time.JD"
                        }
    #mapping_generator.concrete_classes = {}
    #mapping_generator.concrete_types={}
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
        
    