'''
Created on 31 mars 2020

@author: laurentmichel
'''
import json
from astropy.io.votable import parse
from client.inst_builder.column_mapping import ColumnMapping
from client.inst_builder.table_iterator import TableIterator
from client.inst_builder.row_filter import RowFilter
from client.inst_builder import logger
from client.launchers.instance_from_votable import InstanceFromVotable
from client.inst_builder.json_mapping_builder import JsonMappingBuilder
from client.inst_builder.instancier import Instancier
from copy import deepcopy
from utils.dict_utils import DictUtils

class VodmlInstance(object):
    '''
    classdocs
    '''
    def __init__(self, votable_path):
        #
        # One instancier per TEMPLATES
        # table name taken as keys
        #
        self.instanciers = {}
        self.votable_path = votable_path   
        #
        # Dict translation of the <VODML> block
        #
        self.json_view = {}       
        # Convert the XML mapping block in a dictionary
        self.build_json_view()
        # Make the dictionary  compliant with JSON mapping syntax
        self.build_json_mapping()
        # Build the instancier
        self.build_instancier_map()

        
    def build_json_view(self):
        logger.info("Extracting the VODML block")
        instanceFromVotable = InstanceFromVotable(self.votable_path)
        instanceFromVotable._extract_vodml_block()
        logger.info("Validating the VODML block")
        instanceFromVotable._validate_vodml_block()
        logger.info("Extracting the raw JSON block")        
        self.json_view = instanceFromVotable.json_block     

    def build_json_mapping(self):
        logger.info("Formating the JSON view")
        builder = JsonMappingBuilder(json_dict=self.json_view )
        builder.revert_compositions("COMPOSITION")
        builder.revert_templates()
        builder.revert_elements("INSTANCE")
        builder.revert_elements("VALUE")
        self.json_view = builder.json

    def build_instancier_map(self):
        logger.info("Looking for tables matching TEMPLATES ")
        votable = parse(self.votable_path)
        for template_key in self.json_view["VODML"]["TEMPLATES"].keys():
            logger.info("Looking for a table matching TEMPLATES %s", template_key)

            name = None
            parsed_table = None
            for table in votable.iter_tables():
                if  template_key == table.ID:
                    logger.info("Table with ID = %s found", template_key)
                    name = table.ID
                    parsed_table = table
                    break
            if name == None:
                for table in votable.iter_tables():
                    if  template_key == table.name:
                        logger.info("Table with name = %s found", template_key)
                        name = table.name
                        parsed_table = table
                        break
            if name == None:
                raise Exception("Cannot find table with name or ID = " + name)
            else:
                logger.info("Add Instancier for table %s", name)
                self.instanciers[template_key] = Instancier(
                    template_key,
                    self.votable_path, 
                    parsed_table=parsed_table,
                    json_inst_dict=self.json_view)

    def populate_templates(self, resolve_refs=False):
        for k,v in self.instanciers.items():
            logger.info("populate template %s", k)
            v.resolve_refs_and_values(resolve_refs=resolve_refs)
            v.map_columns()        
 
    def connect_join_iterators(self):
        parse_tables = {}
        for template,instancier in self.instanciers.items():
            parse_tables[template] = instancier.parsed_table
            
        for template,instancier in self.instanciers.items():
            for target,join_iterator in instancier.join_iterators.items():
                logger.info("join template %s with template %s", template, target)
                join_iterator.connect_votable(parse_tables[target])

    
    
    def _set_header_values(self, root_element):
        """
        The term header refers to the model leaves that must be set with <PARAM> values
        Recursive function
        """
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._set_header_values(root_element[idx])
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                # TODO For now PARAMS references in ARRY are not supported
                if k == 'ARRAY':
                    pass
                else:
                    if isinstance(v, list):
                        for ele in v:
                            self._set_header_values(ele)
                    elif isinstance(v, dict):  
                        self._resolve_header_value(v)
                        self._set_header_values(v)


    def _set_array_iterators(self, root_element):
        """
        Build and array iterator for each ARRAY element found within root_element
        """
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._set_array_iterators(root_element[idx])
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                if k == 'ARRAY':
                    self.array = v 
                    row_filter = None
                    iterator_key = None
                    for ro in self.array.keys():
                        if ro == 'FILTER' :
                            row_filter = RowFilter(self.array['FILTER'])
                            iterator_key = row_filter.name
                            logger.info("Add filter %s", row_filter.name)
                        else:
                            iterator_key = ro
                            logger.info("Set table iterator for object with role=%s", ro)
                            self.table_iterators[ro] = TableIterator(
                                iterator_key,
                                self.votable.to_table(), 
                                self.array[ro],
                                self.column_mapping,
                                row_filter=row_filter
                                )
                    pass
                else:
                    if isinstance(v, list):
                        for ele in v:
                            self._set_array_iterators(ele)
                    elif isinstance(v, dict):  
                        #self._set_value(v)
                        self._set_array_iterators(v)

    def _set_array_subelement_values(self, array_element, parent_role=None):
        """
        Recursive function
        Takes all @ref of the array_element content and map them the table columns
        """
        if isinstance(array_element, list):
            for idx, _ in enumerate(array_element):
                if self.retour is None:
                    self._set_array_subelement_values(array_element[idx])
        elif isinstance(array_element, dict):
            for k, v in array_element.items():
                if isinstance(v, list):
                    for ele in v:
                        self._set_array_subelement_values(ele)
                elif isinstance(v, dict): 
                    self._set_value(v, role=k, parent_role=parent_role)
                    self._set_array_subelement_values(v, parent_role=k)
     
    def _get_subelement_by_role(self, root_element, searched_role):
        """
        Store in self.searched_elements all elements with @dmrole=searched_role
        """
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._get_subelement_by_role(root_element[idx], searched_role)
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                if k == searched_role:
                    self.searched_elements.append(v)
                if isinstance(v, list):
                    for ele in v:
                        self._get_subelement_by_role(ele, searched_role)
                elif isinstance(v, dict):  
                    self._get_subelement_by_role(v, searched_role)
                    
    def _get_subelement_by_id(self, root_element, searched_id):
        """
        Store in self.searched_ids all elements with @ID=searched_id
        """
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    if self._id_matches(root_element[idx], searched_id):
                        self.searched_ids.append(root_element[idx])
                    self._get_subelement_by_id(root_element[idx], searched_id)
        elif isinstance(root_element, dict):
            if self._id_matches(root_element, searched_id):
                self.searched_ids.append(root_element) 
            for _, v in root_element.items():
                if isinstance(v, list):
                    for ele in v:
                        if self._id_matches(ele, searched_id):
                            self.searched_ids.append(ele)
                        self._get_subelement_by_id(ele, searched_id)
                elif isinstance(v, dict):  
                    if self._id_matches(v, searched_id):
                        self.searched_ids.append(v)
                    self._get_subelement_by_id(v, searched_id)
                    
    def _get_subelement_by_type(self, root_element, searched_type):
        """
        Store in self.searched_types all elements with @dmtype=searched_type
        """
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    if self._type_matches(root_element[idx], searched_type):
                        self.searched_types.append(root_element[idx])
                    self._get_subelement_by_type(root_element[idx], searched_type)
        elif isinstance(root_element, dict):
            if self._type_matches(root_element, searched_type):
                self.searched_types.append(root_element) 
            for _, v in root_element.items():
                if isinstance(v, list):
                    for ele in v:
                        if self._type_matches(ele, searched_type):
                            self.searched_types.append(ele)
                        self._get_subelement_by_type(ele, searched_type)
                elif isinstance(v, dict):  
                    if self._type_matches(v, searched_type):
                        self.searched_types.append(v)
                    self._get_subelement_by_type(v, searched_type)

    def _get_object_references(self, root_element, replacement_list):
        """
        recursive function
        Looks into root_element for element being references (INSTANCE with @dmref)
        Objects found are stored in replacement_list
        
        :param root_element: Root element for the search
        :type root_element: XML element
        :param replacement_list: List of the found elements
        :type replacement_list: list of {"node": element found, "key": role if thye reference, "dmref": instance reference}
        """
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                self._get_object_references(root_element[idx], replacement_list)
        elif isinstance(root_element, dict):
            if self._is_object_ref(root_element):
                pass
            for k , v in root_element.items():
                if isinstance(v, list):
                    for ele in v:
                        self._get_object_references(ele, replacement_list)
                elif isinstance(v, dict):  
                    if self._is_object_ref(v):
                        replacement_list.append(
                            {"node": root_element, 
                             "key": k, 
                             "dmref": v["@dmref"]})
                        return replacement_list
                        #self.searched_types.append(v)
                    self._get_object_references(v, replacement_list)
        return []            

    def _id_matches(self, element, searched_id):
        """
        Returns True if element[@ID] matches id
        """
        if( isinstance(element, dict) and 
            "@ID" in element.keys() and 
            element["@ID"] == searched_id):
            return True
        return False
    
    def _type_matches(self, element, searched_type):
        """
        Returns True if element[@dmtype] matches searched_type
        """
        if( isinstance(element, dict) and 
            "@dmtype" in element.keys() and 
            element["@dmtype"] == searched_type):
            return True
        return False
    
    def _is_object_ref(self, element):
        """
        Returns True if the element is an object reference
        <INSTANCE dmref=xxx/>
        """
        if( isinstance(element, dict) and 
            "@dmref" in element.keys() and 
            "@dmtype" not in element.keys()):
            return True
        return False
    
    def _set_value(self, element, role=None, parent_role=None):
        """
        Create a column mapping entry for the element if it is a @ref
        both role an parent_role are just labels used make more explicit 
        the string representation of the columns mapping
        """
        keys = element.keys()
        if ("@dmtype" in keys and "@ref" in keys 
            and "@value" in keys and element["@value"] == ""):  
            logger.info("Give role %s to the column %s "
                        , parent_role, element["@ref"])
            self.column_mapping.add_entry(element["@ref"], role, parent_role)
            element["@value"] = "array coucou"

    def _resolve_header_value(self, element):
        """
        Set the @value of element with the value of the <PARAM> having 
        either a ID or a name matching @ref 
        """
        keys = element.keys()
        if ("@dmtype" in keys and "@ref" in keys 
            and "@value" in keys and element["@value"] == ""):  
            for param in  self.votable.params:
                if param.ID ==  element["@ref"]:
                    logger.info("set element %s with value=%s of PARAM(ID=%s)"
                                , str(element), param.value, element["@ref"])
                    element["@value"] = param.value.decode("utf-8") 
                elif param.name  ==  element["@ref"] :
                    logger.info("set element%s with value=%s of PARAM(name=%s)"
                                , str(element), param.value, element["@ref"])
                    element["@value"] = param.value.decode("utf-8") 
      
    def map_columns(self):
        self.column_mapping._map_columns(self.votable)
                   
    def _get_next_row_instance(self, data_subset=None):
        if len(self.table_iterators) > 0 :
            for key, value in self.table_iterators.items():
                if data_subset is None or data_subset == key:
                    return value._get_next_row_instance()
            raise Exception("cannot find data subset " + data_subset)
        else:
            print("No data table")
            return {}
    
    def _get_next_flatten_row(self, data_subset=None):
        if len(self.table_iterators) > 0 :
            for key, value in self.table_iterators.items():
                if data_subset is None or data_subset == key:
                    return value._get_next_flatten_row()
            raise Exception("cannot find data subset " + str(data_subset))
        else:
            print("No data table")
            return {}
        
    def rewind(self):
        if len(self.table_iterators) > 0 :
            for _, iterator in self.table_iterators.items():
                iterator._rewind()
        else:
            print("No data table")
            return {}


    def get_flatten_data_head(self, data_subset=None):
        if len(self.table_iterators) > 0 :
            for key, value in self.table_iterators.items():
                if data_subset is None or data_subset == key:
                    return value._get_flatten_data_head()
            raise Exception("cannot find data subset " + str(data_subset))
        else:
            print("No data table")
            return {}

    def get_data_subset_keys(self):
        return self.table_iterators.keys()
           
    def search_instance_by_role(self, searched_role, root_element=None):
        self.searched_elements = []
        if root_element is not None:
            root = root_element
        else:
            root = self.json['VODML']
        self._get_subelement_by_role(root, searched_role)
        
        for idx, ele in enumerate(self.searched_elements):
            if "@ref" in ele.keys():
                self.searched_elements[idx] = self.search_instance_by_id(ele["@ref"])[0]
                
        return self.searched_elements
    
    def resolve_object_references(self):
        #
        # resolve object reference
        # an object reference is something like {"@ref"=xxx}
        # Refrences are replaced with copies of object looking like
        #  {"@ID"=xxx ...}
        #
        root = self.json
        while True:
            replacement_list = []  

            self._get_object_references(root, replacement_list)
            if len(replacement_list) == 0:
                break
            else :
                for replacement in replacement_list:
                    instance = self.search_instance_by_id(replacement["dmref"], root)[0]
                    replacement["node"][replacement["key"]] = deepcopy(instance)
     
    def search_instance_by_id(self, searched_id, root_element=None):
        self.searched_ids = []
        if root_element is not None:
            root = root_element
        else:
            root = self.json['VODML']
        self._get_subelement_by_id(root, searched_id)
        return self.searched_ids
    
    def search_instance_by_type(self, searched_type, root_element=None):
        self.searched_types = []
        if root_element is not None:
            root = root_element
        else:
            root = self.json['VODML']
        self._get_subelement_by_type(root, searched_type)
        return self.searched_types

            