'''
Created on 31 mars 2020

@author: laurentmichel
'''
import json, sys
from astropy.io.votable import parse_single_table
from inst_builder.column_mapping import ColumnMapping
from inst_builder.table_iterator import TableIterator
from inst_builder.row_filter import RowFilter
class Instancier(object):
    '''
    classdocs
    '''
    def __init__(self, votable_path, json_inst_path):
        '''
        Constructor
        '''
        self.votable_path = votable_path
        self.votable = parse_single_table(self.votable_path)
        self.table = self.votable.to_table()
        
        self.json_path = json_inst_path
        with open(self.json_path) as json_file:
            self.json = json.load(json_file)
        self.retour = None
        self.array = None
        self.table_iterators = {}
        self.column_mapping = ColumnMapping()

    def get_elements(self):
        root_element = self.json['VODML']
        self._get_subelement(root_element)

    def get_array_elements(self):
        self._get_array_subelement(self.array)

    def _get_subelement(self, root_element):
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._get_subelement(root_element[idx])
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                if k == 'ARRAY':
                    self.array = v 
                    for ro in self.array.keys():
                        row_filter = None
                        iterator_key = None
                        for rok in self.array[ro][0].keys():
                            if 'FILTER' in self.array[ro][0].keys():
                                print(self.array[ro][0]['FILTER'])
                                row_filter = RowFilter(self.array[ro][0]['FILTER'])
                                iterator_key = row_filter.name
                            else:
                                iterator_key = rok
                        
                        self.table_iterators[iterator_key] = TableIterator(
                            iterator_key,
                            self.votable.to_table(), 
                            self.array[ro],
                            self.column_mapping,
                            row_filter=row_filter
                            )
                        break
                    pass
                else:
                    if isinstance(v, list):
                        for ele in v:
                            self._get_subelement(ele)
                    elif isinstance(v, dict):  
                        self._set_value(v)
                        self._get_subelement(v)

    def _get_array_subelement(self, root_element):
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._get_array_subelement(root_element[idx])
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                if isinstance(v, list):
                    for ele in v:
                        self._get_array_subelement(ele)
                elif isinstance(v, dict):  
                    self._set_value(v, role=k)
                    self._get_array_subelement(v)
     
    def _set_value(self, element, role=None):
        keys = element.keys()
        if ("@dmtype" in keys and "@ref" in keys 
            and "@value" in keys and element["@value"] == ""):        
            self.column_mapping.add_entry(element["@ref"], role)
            element["@value"] = "array coucou"
            

    def _map_columns(self):
        self.column_mapping._map_columns(self.votable)
    
    
    def _get_data_subset_keys(self):
        return self.table_iterators.keys()
          
    def _get_next_row_instance(self, data_subset=None):
        for key, value in self.table_iterators.items():
            if data_subset is None or data_subset == key:
                return value._get_next_row_instance()
        raise Exception("cannot find data subset " + data_subset)
    
    def _get_next_flatten_row(self, data_subset=None):
        for key, value in self.table_iterators.items():
            if data_subset is None or data_subset == key:
                return value._get_next_flatten_row()
        raise Exception("cannot find data subset " + data_subset)
    
    def _get_flatten_data_head(self, data_subset=None):
        for key, value in self.table_iterators.items():
            if data_subset is None or data_subset == key:
                return value._get_flatten_data_head()
        raise Exception("cannot find data subset " + data_subset)

            
 
            