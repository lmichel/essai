'''
Created on 1 avr. 2020

@author: laurentmichel
'''
from utils.dict_utils import DictUtils
class ColumnMapping():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.column_refs = {}
        self.column_ids = {}
       
    def __repr__(self):
        return "column_refs:{} \n column_ids:{}".format(
            self.column_refs,
            self.column_ids)
    
    def add_entry(self, key, role, parent_role=None):
        if key not in self.column_refs.keys():
            self.column_refs[key] = {
                "parent_role": parent_role, 
                "role": role, 
                "index": None, 
                "field": None
                }
      
    def _map_columns(self, votable):
        keys =self.keys()

        indx = 0        
        for field in  votable.fields:
            self.column_ids[indx] = {
                "name": field.name,
                "ref": field.ref,
                "id": field.ID,
                }
            if field.ID in keys :
                self.set_value(field.ID, indx, field)
            elif field.ref in keys :
                self.set_value(field.ref, indx, field)
            elif field.name in keys:
                self.set_value(field.name, indx, field)
            indx += 1
            
        indx = -1
        for param in  votable.params:
            if param.ID in keys :
                self.set_value(param.ID, indx, field)
            elif param.ref in keys :
                self.set_value(param.ref, indx, field)
            elif param.name in keys :
                self.set_value(param.name, indx, field)
    
    def get_col_index_by_name(self, name):
        for k,v in self.column_ids.items():
            if "id" in v.keys() and v["id"] == name:
                return k   
        for k,v in self.column_ids.items():
            if "name" in v.keys() and v["name"] == name:
                return k   
        return None
        
    def set_value(self, key, index, field_or_param):
        self.add_entry(key, None)
        self.column_refs[key]["index"] = index
        self.column_refs[key]["field"] = field_or_param

    def get_index(self, key):
        return self.column_refs[key]["index"]    
    
    def get_field_or_param(self, key):
        return self.column_refs[key]["field"]
    
    def get_key(self, index):
        for k, v in self.column_refs:
            if v["index"] == index:
                return k
        return None
    
    def get_indexes(self):
        retour = []
        for _, v in self.column_refs.items():
            retour.append(v["index"])
        return retour
    
    def get_column_head(self):
        retour = []
        for key, v in self.column_refs.items():
            retour.append("{}({}) [col#{} {}]". format(v["parent_role"], v["role"], v["index"], key))
        return retour
    
    def keys(self):
        return self.column_refs.keys()