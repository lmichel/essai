#!/usr/bin/env python
# -*- coding: utf-8 -*-

from lxml import etree
import sys, urllib, traceback, re
from mappingfactory import logger
import urllib.request



def constant(f):
    def fset(self, value):
        raise TypeError
    def fget(self):
        return f()
    return property(fget, fset)


DEBUG_MODE = False
class _Const(object):
    DEBUG = DEBUG_MODE
    WITH_TYPES = True
    SUBCLASS = "SubC" if DEBUG_MODE == True else ""
    SUBTYPE = "SubT" if DEBUG_MODE == True else ""
    TSUBTYPE = "TSubC" if DEBUG_MODE == True else ""
    '''
    @constant
    def DEBUG(self):
        return DEBUG_MODE
    @constant
    def WITH_TYPES(self):
        return True
    @constant
    def SUBCLASS(self):
        return "SubC" if DEBUG_MODE == True else ""
    @constant
    def SUBTYPE(self):
        return "SubT" if DEBUG_MODE == True else ""
    @constant
    def TSUBTYPE(self):
        return "TSubC" if DEBUG_MODE == True else ""
    '''
CONST = _Const()

class VodmlConstraint:    
    def __init__(self, att_role):
        self._att_role = att_role
        self._subsets = []
        
    def __str__(self):
        retour =  "CONSTRAINT "
        retour += self._att_role + "=>" + str(self._subsets)
        return retour
    
    def add_subset(self, subset):
        if not subset in self._subsets:
            self._subsets.append(subset)
            
    def add_constraint(self, vodmlcontraint):
        datatypes = vodmlcontraint.subsets
        for dt in datatypes:
            self.add_subset(dt)
            
    def __repr__(self):
        return self.__str__()
    
    @property
    def att_role(self):
        return self._att_role
    
    @property
    def subsets(self):
        return self._subsets
    
    @property
    def is_empty(self):
        return True if len(self._subsets) == 0  else False
    
    @property
    def is_single(self):
        return True if len(self._subsets) == 1  else False

    @property    
    def length(self):
        return len(self._subsets)

    
class VodmlConstraintDict:    
    def __init__(self):
        self._constraints = dict()
        
    def __str__(self):
        retour =  "CONSTRAINTS [ "
        for r, vc in self._constraints.items():
            retour += r + ": " + vc.__str__() + " "
        retour += "]"
        return retour
    
    def __repr__(self):
        return self.__str__()
    
    def add_contraint(self, vodml_contraint):
        if( vodml_contraint.is_empty ):
            return
        if( isinstance(vodml_contraint, list)):
            raise Exception("===============")
        if vodml_contraint and not vodml_contraint.is_empty :
            print("@@@@@@@@@@@ add " + self.__str__())
            print("@@@@@@@@@@@ add " + vodml_contraint.__str__())
            att_role = vodml_contraint.att_role
            subtypes = vodml_contraint.subsets
            if att_role not in self._constraints.keys():
                print("1")
                self._constraints[att_role] = vodml_contraint
                print("1 " + self._constraints[att_role].__repr__())
                print("1 " + self.__repr__())

            else :
                print("2")
                ar = self._constraints[att_role]
                print(ar)
                for st in vodml_contraint.subsets:
                    if st not in ar.subsets:
                        ar.add_subset(st) 
    
    @property
    def constraints(self):
        return self._constraints
    
    def keys(self):
        return self._constraints.keys()
    
    def merge(self, vodml_constraint_dict):
        print("@@@@@@@@@@@ merge 0" + vodml_constraint_dict.__str__())
        print("@@@@@@@@@@@ merge 1" + self.__str__())
        for k, vodml_constraint in vodml_constraint_dict.constraints.items():
            print("@@@@@@@@@@@ merge 1.K" + vodml_constraint.__str__())
            if k not in self._constraints.keys():
                self._constraints[k] = vodml_constraint
            else:
                self.add_contraint(vodml_constraint)
        print("@@@@@@@@@@@ merge 2" + self.__str__())

    
class VodmlObjectType:
    '''
    
    '''
    def __init__(self, vomdlid, name, superclass, attributes, abstract=False, vodml_constraints=VodmlConstraintDict() ):
        self.vodmlid = vomdlid
        self.name = name
        self.superclass = superclass
        self.attributes = attributes
        self.abstract = abstract
        self.vodml_constraints = vodml_constraints
        
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
        retour += "CONSTRAINTS " + self.vodml_constraints.__repr__()
        return retour
    
    def __repr__(self):
        return self.__str__()
                     
class VodmlDataType:
    def __init__(self, vodmlid, name, ref, superclass, attributes, abstract=False,vodml_constraints=VodmlConstraintDict() ):
        '''
        :param vodmlid:
        :type vodmlid:
        :param name:
        :type name:
        :param ref:
        :type ref:
        :param superclass:
        :type superclass:
        :param attributes:
        :type attributes:
        :param abstract:
        :type abstract:
        :param vodml_constraints:
        :type vodml_constraints: VodmlConstraintDict
        '''
        self.vodmlid = vodmlid
        self.ref = ref
        self.name = name
        self.superclass = superclass
        self.attributes = attributes
        self.abstract = abstract
        self.vodml_constraints = vodml_constraints 
        
    def __str__(self):
        return self.__repr__()
    def __repr__(self):
        retour = ""
        if self.abstract:
            retour += "Abstract "
        retour +=  "DATATYPE " + self.name + " vodmlid=" + self.vodmlid + " ref" + self.ref 
        retour += "\n    ATTRIBUTES " + self.attributes.__repr__()
        retour += "\n    " + self.vodml_constraints.__repr__()
        return retour
        
        
class VodmlAttribute:    
    def __init__(self, vodmlid, name, dmtype, array_size, is_reference):
        self.vodmlid = vodmlid
        self.name = name
        self.dmtype = dmtype
        self.array_size = array_size
        self.is_reference = is_reference
        if dmtype == "":
            logger.info(name + " " + vodmlid + " has not dmtype" )

    def __str__(self):
        retour =  "ATTRIBUTE "
        retour += "(array_size " + str(self.array_size) + ") "
        retour += self.name + " " + self.vodmlid +" dmtype=" +self.dmtype.__str__()
        return retour
    def __repr__(self):
        return self.__str__()
    



class VodmlImport:
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
        self.last_object_mapped = None

    def read_package(self, model_name, package_node):
        if package_node.tag == 'package':
            package_name =  package_node.find("name").text
            self.log_msg ("read_package" , "Reading package " + package_name)
            for child in package_node.findall('*'):
                if child.tag == "objectType":
                    ot = self.read_object_type(model_name, child)
                    self.log_msg("read_package" ,"ADD OBJECT " + ot.__repr__())
                    self.object_types[ot.vodmlid] = ot
                elif child.tag == 'primitiveType'   or child.tag == 'enumeration':
                    dt = self.read_data_type(model_name, child)
                    self.log_msg("read_package" ,"ADD PRIM TYPE " + dt.__repr__())
                    self.primitive_types[dt.vodmlid] = dt
                elif child.tag == "dataType" :
                    ot = self.read_data_type(model_name, child)
                    self.log_msg("read_package" ,"ADD DATA TYPE " + ot.__repr__())
                    self.data_types[ot.vodmlid] = ot
                elif child.tag == 'package':
                    self.read_package(model_name, child)

    def read_data_type(self, model_name,datatype_tag):
        vodmlid = ''
        name = ''
        superclass = ''
        ref = ''
        attributes = dict()
        constraints = VodmlConstraintDict()
        abstract = False
        if datatype_tag.get("abstract") == "true":
            abstract = True
        for datatype_tag_child in datatype_tag.findall('*'):
            if datatype_tag_child.tag == "vodml-id":
                vodmlid = model_name + ":" + datatype_tag_child.text
                self.log_msg("read_data_type", "READING DATA TYPE " + vodmlid)

            elif datatype_tag_child.tag == "name":
                name = datatype_tag_child.text
            elif datatype_tag_child.tag == "vodml-ref":
                ref = datatype_tag_child.text
            elif datatype_tag_child.tag == "attribute" or datatype_tag_child.tag == "reference" :
                att = self.read_attribute(model_name,datatype_tag_child)
                attributes[att.vodmlid] = att
            elif datatype_tag_child.tag == "extends":
                for super_type_child in datatype_tag_child.findall('vodml-ref'):
                    superclass = super_type_child.text
                    break
            elif datatype_tag_child.tag == "constraint":
                ct = self.read_constraint(model_name,datatype_tag_child)
                print(ct)
                constraints.add_contraint(ct)
 
            elif datatype_tag_child.tag == "reference":
                att = self.read_reference(model_name,datatype_tag_child)
                attributes[att.vodmlid] = att
       
        retour = VodmlDataType(vodmlid, name, ref, superclass, attributes, abstract,constraints)
        if vodmlid == "coords:BinnedCoordValue":
            print(constraints)
            print(retour)
            #sys.exit() 
        return retour

    def read_attribute(self, model_name,attribute_tag):
        dmtype = ''
        array_size = 2
        for attribute_tag_child in attribute_tag.findall('*'):
            if attribute_tag_child.tag == "vodml-id":
                vodmlid = model_name + ":" + attribute_tag_child.text
            elif attribute_tag_child.tag == "name":
                name = attribute_tag_child.text
            elif attribute_tag_child.tag == "multiplicity":
                for multiplicity_tag_child in attribute_tag_child.findall('maxOccurs'):
                    array_size = int(multiplicity_tag_child.text)
            elif attribute_tag_child.tag == "datatype" or attribute_tag_child.tag == 'reference':
                for vodml_ref_tag_child in attribute_tag_child.findall('vodml-ref'):
                    dmtype = vodml_ref_tag_child.text                
 
        return VodmlAttribute(vodmlid, name, dmtype, array_size, False)
         
         
     
    def read_reference(self, model_name,reference_tag):
        for reference_tag_child in reference_tag.findall('*'):
            if reference_tag_child.tag == "vodml-id":
                vodmlid = model_name + ":" + reference_tag_child.text
            elif reference_tag_child.tag == "name":
                name = reference_tag_child.text
            elif reference_tag_child.tag == "datatype":
                for vodml_ref_tag_child in reference_tag_child.findall('vodml-ref'):
                    dmtype = vodml_ref_tag_child.text                
                
        return VodmlAttribute(vodmlid, name, dmtype, 1, True)

    def read_constraint(self, model_name,constraint_tag):  
        role=""
        datatype = ""  
        retour = None
        for constraint_tag_child in constraint_tag.findall('*'):
            if constraint_tag_child.tag == "role":
                for constraint_tag_sub_child in constraint_tag_child.findall('*'):
                    if constraint_tag_sub_child.tag == "vodml-ref":
                        role = constraint_tag_sub_child.text

            elif constraint_tag_child.tag == "datatype":
                for constraint_tag_sub_child in constraint_tag_child.findall('*'):
                    if constraint_tag_sub_child.tag == "vodml-ref":
                        datatype = constraint_tag_sub_child.text

        retour = VodmlConstraint(role)
        retour.add_subset(datatype)
        print("read_constraint " + retour.__repr__()   )            
        return retour        

     
    def read_object_type(self, model_name, object_type_tag):
        attributes = dict()
        constraints = VodmlConstraintDict()
        superclass = ''
    
        abstract = False
        if object_type_tag.get("abstract") == "true":
            abstract = True
        for object_type_child in object_type_tag.findall('*'):
            if object_type_child.tag == "vodml-id":
                vodmlid = model_name + ":" + object_type_child.text
                self.log_msg("read_object_type", "READING OBJECT TYPE " + vodmlid)
   
            elif object_type_child.tag == "name":
                name = object_type_child.text
            elif object_type_child.tag == "composition" or object_type_child.tag == "attribute" :
                #or multiplicity_tag_child in object_type_child.findall('maxOccurs'):
                #    array_size = int(multiplicity_tag_child.text)
                att = self.read_attribute(model_name,object_type_child)
                attributes[att.vodmlid] = att
            elif object_type_child.tag == "extends":
                for super_type_child in object_type_child.findall('vodml-ref'):
                    superclass = super_type_child.text
                    break
            elif object_type_child.tag == "reference":
                att = self.read_reference(model_name,object_type_child)
                attributes[att.vodmlid] = att

            elif object_type_child.tag == "constraint":
                ct = self.read_constraint(model_name,object_type_child)
                constraints.add_contraint(ct)

        obj = VodmlObjectType(vodmlid, name, superclass, attributes, abstract, constraints)
 
        return obj
                      
    
    def parse_vodml_file(self, filename='', model=None):
        self.log_msg("parse_vodml_file", "Reading FILE " + filename + " for model " + model)
    
        if filename.startswith("http://") or filename.startswith("https://") :
            tree = etree.ElementTree(file=urllib.request.urlopen(filename))
        else :
            tree = etree.parse(filename)
        
        root = tree.getroot()
        ns = {"vo-dml": "http://www.ivoa.net/xml/VODML/v1"}
        if model == None:
            model_name =  root.find("name").text
        else :
            model_name = model
    
        self.log_msg("parse_vodml_file", "PARSE FILE " + filename + " for model "+ model)
            
        for node in tree.xpath("import"):
            name = node.find('name').text
            url = node.find('url').text
            if not name in self.imports:
                # Let's take the import name as VODML prefix
                model_name = name
                self.log_msg ("parse_vodml_file","PARSE IMPORT " + name)
                self.parse_vodml_file(filename=url, model=model_name)
                self.log_msg ("parse_vodml_file","END PARSE " + name)   
                self.imports[name]=url
    
        if model == None:
            model_name =  root.find("name").text
        else :
            model_name = model
    
        for node in tree.xpath("*"):
            if node.tag == 'package':
                self.read_package(model_name, node)
            elif node.tag == 'primitiveType'  or node.tag == 'enumeration':
                dt = self.read_data_type(model_name, node)
                self.log_msg("parse_vodml_file","ADD primitiveType " + dt.vodmlid)
                self.primitive_types[dt.vodmlid] = dt
            elif node.tag == 'dataType' or node.tag == 'reference':
                dt = self.read_data_type(model_name, node)
                self.log_msg("parse_vodml_file","ADD dataType " + dt.__repr__())
                self.data_types[dt.vodmlid] = dt
            elif node.tag == 'objectType':
                dt = self.read_object_type(model_name, node)
                self.log_msg("parse_vodml_file","ADD objectType " + dt.vodmlid)
#                 if  dt.vodmlid == "coords:Axis":
#                     print(dt.__repr__())
#                     sys.exit()
                self.object_types[dt.vodmlid] = dt
    
    def generate_single_attribute_mapping(self, attribute):
        ref = attribute.dmtype
        ars = ""

        if attribute.array_size != 1:
            ars = "array_size=" +  str(attribute.array_size) 
        if ref in self.primitive_types:
            self.append_xml( "<VALUE dmrole='" + attribute.vodmlid + "' dmtype='" + ref +  "' " + ars + " ref='@@@@@@'></VALUE>")
        elif ref in self.data_types :
            dt = self.data_types[ref]
            self.generate_data_mapping(dt, attribute.vodmlid)
        elif ref in self.object_types :
            self.generate_object_mapping(self.object_types[ref], attribute.vodmlid)
        else:
            self.log_error("type " + ref + " of attribute [" + attribute.__repr__() + "] not found")
            
    def generate_attribute_mapping(self, attribute):
 
        if attribute.array_size == 0 :
            return
        elif attribute.array_size == 1:
            self.generate_single_attribute_mapping(attribute)
        elif attribute.array_size == -1:
            print(attribute.__repr__())
            #sys.exit(1)
            self.append_xml( "<COLLECTION  size='-1' dmrole='" + attribute.vodmlid + "'>")                    
            self.generate_single_attribute_mapping(attribute)
            self.append_xml( "</COLLECTION>")
        else:
            self.append_xml( "<COLLECTION  size='" + str(attribute.array_size) + "'>")
            for i in range(0,attribute.array_size ):

            #for num in range(0, attribute.array_size):
            #    print("======== add " + attribute.__repr__())
                self.generate_single_attribute_mapping(attribute)
            self.append_xml( "</COLLECTION>")
       
     
        
    def generate_data_mapping(self, vodml_data_type, vodml_id):
        am = ""
        sc_flag = ""
        dmtype = " dmtype='" + vodml_data_type.vodmlid + "'"
        if vodml_data_type.abstract:
            self.mapped_abstract_types.append(vodml_data_type.vodmlid)
            am =  " abstract='true' "
            dmtype = " dmtype='" + vodml_data_type.vodmlid + "'"
            if vodml_id in self.concrete_types.keys():
                vodml_data_type = self.data_types[self.concrete_types[vodml_id]]
                sc_flag = "(" + CONST.SUBTYPE  + " " + vodml_data_type.vodmlid + ")" if  CONST.SUBTYPE != "" else ""
                dmtype = " dmtype='" + sc_flag + vodml_data_type.vodmlid + "'"
    
                if vodml_data_type.abstract == False:
                    am = ""
                else:
                    self.mapped_abstract_types.append(vodml_data_type.vodmlid)

        self.append_xml( "<INSTANCE dmrole='" + vodml_id + "' " + dmtype + " " + am + ">")
        for attribute in vodml_data_type.attributes.values():
            self.generate_attribute_mapping(attribute)
        self.append_xml( "</INSTANCE>")
    
    def get_single_oject_mapping(self, vodml_object_type, dmrole, qualifiers):       
        str = "<INSTANCE dmrole='" + dmrole + "' " + qualifiers + ">"
        self.append_xml(str)
        read_attributes = []
        in_loop = False
        '''
        If the same vodmlid is used twice successively, we are likely in forever loop
        such as TimeStamp>TimeFrame..
        in this case, we put a reference on the class instead of parsing it again and
        to run in stack overflow
        '''
        if self.last_object_mapped == vodml_object_type.vodmlid:
            in_loop = True
        self.last_object_mapped = vodml_object_type.vodmlid

        for attribute in vodml_object_type.attributes.values():
            if  attribute.vodmlid not in read_attributes:
                if (in_loop == True and attribute.dmtype in self.object_types)  or attribute.is_reference == True:
                    print(in_loop)
                    self.generate_object_mapping(self.object_types[attribute.dmtype], attribute.vodmlid)
                else :
                    self.generate_attribute_mapping(attribute)
                    read_attributes.append(attribute.vodmlid)
            else:
                pass
                #print( "SKIPPP " + attribute.vodmlid )
        self.append_xml( "</INSTANCE>")

            
    def generate_object_mapping(self, vodml_object_type, vodml_id):
        am = ""
        sc_flag = "";
        type = ""
        print("@@@@@@@@@@@@@@@@ " + vodml_id)
        if "coords:Coordinate.frame" == vodml_id:
            print("stop")
            
        if vodml_id in self.concrete_classes.keys():
            if  isinstance(self.concrete_classes[vodml_id], list):
                for class_id in self.concrete_classes[vodml_id]:
                    sc_flag = "(" + CONST.TSUBTYPE  + " " + vodml_object_type.vodmlid + ")" if  CONST.TSUBTYPE != "" else ""
                    vodml_object_type = self.object_types[class_id]
                    type = " dmtype='" + sc_flag + vodml_object_type.vodmlid + "'"
                    self.get_single_oject_mapping(vodml_object_type,vodml_id,  type + " " + am)   

                    if vodml_object_type.abstract == True:
                        self.mapped_abstract_classes.append(vodml_object_type.vodmlid)
            else:
                vodml_object_type = self.object_types[self.concrete_classes[vodml_id]]
                sc_flag = "(" + CONST.SUBCLASS  + " " + vodml_object_type.vodmlid + ")" if  CONST.SUBCLASS != "" else ""
                type = " dmtype='" + sc_flag + vodml_object_type.vodmlid + "'"
                if vodml_object_type.abstract == False:
                    pass
                    #am = ""
                    #type = ""
                else:
                    self.mapped_abstract_classes.append(vodml_object_type.vodmlid)
                self.get_single_oject_mapping(vodml_object_type,vodml_id,  type + " " + am)     
        else :
            type = " dmtype='"  + vodml_object_type.vodmlid + "'" if CONST.WITH_TYPES else ""
            self.get_single_oject_mapping(vodml_object_type,vodml_id,  type + " " + am)     
    
    def get_sub_classes(self, super_class_id):
        retour = [];
        for k, v in self.object_types.items():
            if v.superclass == super_class_id:
                retour.append(k)
        return retour;
    
    def get_sub_types(self, super_class_id):
        retour = [];
        for k, v in self.data_types.items():
            if v.superclass == super_class_id:
                retour.append(k)
        return retour;
    
    def append_xml(self, string):
        self.xml_string +=  string  
        return
    
    def log_msg(self, label, msg):
         print(label + ": " + msg)
         
    def log_error(self, msg):
        self.log_msg("FATAL ERROR", msg)
        traceback.print_stack()
        sys.exit(1)    
    
    def resolve_constaints(self):
         for obj in  self.object_types.values():
            for attribute in obj.attributes.values():
                if attribute.vodmlid in obj.vodml_constraints.keys():
                    print(obj.vodml_constraints.constraints)
                    vodml_constraint = obj.vodml_constraints.constraints[attribute.vodmlid]
                    if vodml_constraint.is_single :
                        new_type = vodml_constraint.subsets[0]
                        if new_type in self.data_types.keys():
                            attribute.dmtype = self.data_types[new_type].vodmlid
                        elif new_type in self.object_types.keys():
                            attribute.dmtype = self.object_types[new_type].vodmlid
                            self.log_msg("resolve_constaints", "Constraint found in object type " + obj.vodmlid + " " + attribute.vodmlid + "==>" + new_type)
                    else:
                        self.log_msg("resolve_constaints", "Constraint multiple found in object type " + obj.vodmlid + " " + attribute.vodmlid + "==>" + vodml_constraint.subsets)

         for obj in  self.data_types.values():
            for attribute in obj.attributes.values():
                if attribute.vodmlid in obj.vodml_constraints.keys():
                    if attribute.vodmlid == "coords:Coordinate.frame" :
                        print("on y est")
                    print(obj.vodml_constraints.constraints)
                    vodml_constraint = obj.vodml_constraints.constraints[attribute.vodmlid]
                    if vodml_constraint.is_single :
                        new_type = vodml_constraint.subsets[0]
                        if new_type in self.data_types.keys():
                            attribute.dmtype = self.data_types[new_type].vodmlid
                        elif new_type in self.object_types.keys():
                            attribute.dmtype = self.object_types[new_type].vodmlid
                            self.log_msg("resolve_constaints", "Constraint found in data type " + obj.vodmlid + " " + attribute.vodmlid + "==>" + new_type)
                    else:
                        self.log_msg("resolve_constaints", "Constraint multiple found in data type " + obj.vodmlid + " " + attribute.vodmlid + "==>" + vodml_constraint.subsets)

    '''
         for obj in  self.subsets.values():
            print("===================================")
            print(obj)
            for attribute in obj.attributes.values():
                if attribute.vodmlid in obj.vodml_constraints.keys():
                    if attribute.vodmlid == "coords:Coordinate.frame" :
                        print("on y est")
                    vodml_constraint = obj.vodml_constraints.constraints[attribute.vodmlid]
                    if vodml_constraint.is_single :
                        new_type = vodml_constraint.subsets[0]
                        if new_type in self.subsets.keys():
                            attribute.dmtype = self.subsets[new_type].vodmlid
                            print("====================2 " + str(self.subsets[new_type].vodmlid))
                        elif new_type in self.object_types.keys():
                            attribute.dmtype = self.object_types[new_type].vodmlid
                            print("====================1 " + str(self.object_types[new_type].vodmlid))
                        self.log_msg("resolve_constaints", "Constraint found in datatype " + obj.vodmlid + " " + attribute.vodmlid + "==>" + new_type)
                    else:
                        self.log_msg("resolve_constaints", "Constraint multiple found in data type " + obj.vodmlid + " " + attribute.vodmlid + "==>" + vodml_constraint.subsets)
                   
    '''               
    def resolve_inheritance(self):
        for obj in  self.data_types.values():
            superclass = obj.superclass
            while superclass != '':
                sup_obj= None
                if superclass in self.data_types :
                    sup_obj = self.data_types[superclass]
                elif superclass in self.primitive_types :
                    sup_obj = self.primitive_types[superclass]
                else:
                    self.log_error( superclass + " Neither in dataType nor in primitive types")
                for k,v in sup_obj.attributes.items():
                    obj.attributes[k] = v
                print(sup_obj.vodml_constraints)
                print("@@@@@@ resolve_inheritance merge data "  +  sup_obj.vodmlid + "  " + obj.vodmlid)
                obj.vodml_constraints.merge(sup_obj.vodml_constraints)

                #for k,v in sup_obj.vodml_constraints.constraints.items():
                #    print(sup_obj.vodml_constraints.constraints)
                #    obj.vodml_constraints.merge(v)
                superclass = sup_obj.superclass


        #for obj in  self.object_types.values():
        for vodmlid in self.object_types.keys():
            obj = self.object_types[vodmlid]
            superclass = obj.superclass
            while superclass != '':
                sup_obj= None            
                if superclass in self.object_types :
                    sup_obj = self.object_types[superclass]
                elif superclass in self.data_types :
                    sup_obj = self.data_types[superclass]
                else:
                    self.log_error( superclass + " Neither in dataType or objectType")

                for k,v in sup_obj.attributes.items():
                    obj.attributes[k] = v
                #for k,v in sup_obj.vodml_constraints.items():
                #    obj.vodml_constraints[k] = v
                print("@@@@@@ resolve_inheritance merge obs " +  sup_obj.vodmlid + "  " + obj.vodmlid)
                obj.vodml_constraints.merge(sup_obj.vodml_constraints)
                superclass = sup_obj.superclass
            
    def generate_mapping(self):
        self.log_msg("generate_mapping" , "root object is " + self.root_object_id)
        root_object_type = self.object_types[self.root_object_id]
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
        self.generate_object_mapping(root_object_type, 'root')
        self.append_xml("</TEMPLATES>")
        self.append_xml("</VODML>")
