'''
Created on 26 mars 2020

@author: laurentmichel
'''
import json
from copy import deepcopy

class Builder():
    '''
    classdocs
    '''


    def __init__(self, json_path=None, json_dict=None):
        '''
        Constructor
        '''
        if  json_path is not None:
            self.json_path = json_path
            with open(json_path) as json_file:
                self.json = json.load(json_file)
        else:
            self.json = json_dict
            self.json_path = None
        
    def revert_elements(self, name, dmrole=None, dmtype=None):
        root_element = self.json['VODML']
        while True:
            self.retour = None
            self._revert_subelement(root_element, name, dmrole, dmtype)
            if self.retour is not None:
                self.retour["node"].pop(name)
                for k, v in self.retour["newcontent"].items():
                    self.retour["node"][k] = v
            else:
                break
            
    def revert_compositions(self, name, dmrole=None, dmtype=None):
        root_element = self.json['VODML']
        while True:
            self.retour = None
            self._revert_composition(root_element, name, dmrole, dmtype)
            if self.retour is not None:
                self.retour["node"].pop(name)

                for ele in self.retour["newcontent"]:
                    for k, v in ele.items():
                        self.retour["node"][k] = v
            else:
                break
            
    def revert_sets(self, name, default_key=None):
        root_element = self.json['VODML']
        while True:
            self.retour = None
            self._revert_set(root_element, name, default_key)
            if self.retour is not None:
                self.retour["node"].pop(name)
                for k, v in self.retour["newcontent"].items():
                    self.retour["node"][k] = v
            else:
                break
            
    def revert_array(self):
        root_element = self.json['VODML']
        while True:
            self.retour = None

            self._revert_set(root_element, 'ARRAY', None)
            if self.retour is not None:
                array_container = self.retour["node"]
                array_node = array_container['ARRAY']
                array_role = array_node["@dmrole"]
                array_container['ARRAY'] = {array_role: [array_node['INSTANCE']]}
            else:
                break
    
    def _revert_set(self, root_element, name, default_key):
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._revert_set(root_element[idx], name, default_key)
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                if k == name:
                    if isinstance(v, list):
                        raise Exception(name + " cannot be a list")
                    elif isinstance(v, dict):  
                        # the elememt may have been reverted in a precedent pass
                        # It can not be reserved twice
                        try:
                            newcontent = {}
                            new_key = self._get_key_for_element(v, default_key)
                            newcontent[new_key] = deepcopy(v["INSTANCE"])
                            self.retour = {'node': root_element, "newcontent": newcontent}
                        except:
                            pass
                if self.retour is None:
                    self._revert_set(v, name, default_key)
       
    def _revert_subelement(self, root_element, name, dmrole, dmtype):
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._revert_subelement(root_element[idx], name, dmrole, dmtype)
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                if k == name:
                    if isinstance(v, list):
                        newcontent = {}
                        for ele in v:
                            new_key = self._get_key_for_element(ele)
                            newcontent[new_key] = deepcopy(ele)
                            self._drop_role_if_needed(newcontent[new_key])
                            self._add_value_if_needed(newcontent[new_key])
                        self.retour = {'node': root_element, "newcontent": newcontent}
                    elif isinstance(v, dict):  
                        newcontent = {}
                        new_key = self._get_key_for_element(v)
                        newcontent[new_key] = deepcopy(v)
                        self._add_value_if_needed(newcontent[new_key])
                        self._drop_role_if_needed(newcontent[new_key])
                        self.retour = {'node': root_element, "newcontent": newcontent}

                if self.retour is None:
                    self._revert_subelement(v, name, dmrole, dmtype)
                    
    def _revert_composition(self, root_element, name, dmrole, dmtype):
        if isinstance(root_element, list):
            for idx, _ in enumerate(root_element):
                if self.retour is None:
                    self._revert_composition(root_element[idx], name, dmrole, dmtype)
        elif isinstance(root_element, dict):
            for k, v in root_element.items():
                if k == name:

                    if isinstance(v, list):
                        newcontent = []
                        for ele in v:
                            new_key = self._get_key_for_element(ele)
                            ele_cp = deepcopy(ele)
                            self._drop_role_if_needed(ele_cp)
                            if ele_cp:
                                newcontent.append({new_key: [ele_cp]})
                            else :
                                newcontent.append({new_key: []})
                        self.retour = {'node': root_element, "newcontent": newcontent}
                    elif isinstance(v, dict):  
                        newcontent = []
                        ele_cp = deepcopy(v)
                        new_key = self._get_key_for_element(ele_cp)
                        self._drop_role_if_needed(ele_cp)

                        if ele_cp:
                            newcontent.append({new_key: [ele_cp]})
                        else :
                            newcontent.append({new_key: []})

                        self.retour = {'node': root_element, "newcontent": newcontent}

                if self.retour is None:
                    self._revert_composition(v, name, dmrole, dmtype)
                    
    def _add_value_if_needed(self, element):
        keys = element.keys()
        if  "@dmtype" in keys and "@ref" in keys and "@value" not in keys:
            element["@value"] = ""
    
    def _drop_role_if_needed(self, element):
        keys = element.keys()
        if  "@dmrole" in keys :
            element.pop("@dmrole")   
        if  "@size" in keys :
            element.pop("@size")   
                            
    def _get_key_for_element(self, element, default_key=None):
        if default_key is not None:
            return default_key
        new_key = ''
        if "@dmrole" in element.keys():
            new_key = element["@dmrole"]
        if new_key == '' and "@ID" in element.keys():
            new_key = element["@ID"]
        if new_key == '' and "NAME" in element.keys():
            new_key = element["NAME"]
        if new_key == '':
            raise Exception("Cannot compute new key")
        return new_key

    
    def save_instance(self):        
            with open(self.json_path.replace(".json", ".inst.json"), 'w') as jsonfile:
                jsonfile.write(json.dumps(self.json
                                          , indent=2, sort_keys=True)
                                          )
