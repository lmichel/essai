'''
Created on 31 mars 2020

@author: laurentmichel
'''
import json
from astropy.io.votable import parse_single_table
from client.inst_builder.column_mapping import ColumnMapping
from client.inst_builder.table_iterator import TableIterator
from client.inst_builder.row_filter import RowFilter
from client.inst_builder import logger

from copy import deepcopy
from utils.dict_utils import DictUtils

class Instancier(object):
    '''
    classdocs
    '''
    def __init__(self, votable_path, 
                 json_inst_path=None , 
                 json_inst_dict=None):
        '''
        Constructor
        '''
        self.votable_path = votable_path
        self.votable = parse_single_table(self.votable_path)
        self.table = self.votable.to_table()
        
        if json_inst_path is not None:
            self.json_path = json_inst_path
            with open(self.json_path) as json_file:
                self.json = json.load(json_file)
        else:
            self.json = json_inst_dict
            self.json_path = None
        
        #Field block storing the outcome of recursive functions 
        self.retour = None
        self.searched_elements = []
        self.searched_ids = []
        self.searched_types = []
        self.array = None
        # key = role of the instance within ARRAY value = TableIterator
        self.table_iterators = {}
        self.column_mapping = ColumnMapping()

    def resolve_refs_and_values(self, resolve_refs=False):
        root_element = self.json['VODML']
        if resolve_refs is True:
            logger.info("Replace object references with referenced object copies")
            self.resolve_object_references()
        logger.info("Resolve references to PARAMS")
        self._set_header_values(root_element)
        logger.info("Set array iterators")
        self._set_array_iterators(root_element)
        logger.info("Resolve mapping leaves is an ARRAY block")
        self._set_array_subelement_values(self.array, parent_role=None)

        
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

            