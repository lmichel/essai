'''
Created on 30 juin 2020

@author: laurentmichel
'''
from client.inst_builder.table_iterator import TableIterator
from client.inst_builder.row_filter import RowFilter
from utils.dict_utils import DictUtils

class JoinIterator(object):
    '''
    classdocs
    '''
    def __init__(self, 
                 foreign_table,
                 primary_key, 
                 foreign_key,
                 json_join_content):
        self.foreign_table = foreign_table
        self.primary_key = primary_key
        self.foreign_key = foreign_key
        self.json_join_content = json_join_content
        self.instancier = None
        self.parsed_table = None
        #print(DictUtils.get_pretty_json(self.json_join_content))
        
    def __repr__(self):
        return "Join iterator f_table={} p_key={}, f_key={}".format(
            self.foreign_table, self.primary_key, self.foreign_key)
        
    def connect_votable(self, parsed_table):
        from client.inst_builder.instancier import Instancier
        self.parsed_table = parsed_table
        ack = None
        acv = None
        for k in self.json_join_content.keys():
            if k.startswith("@") is False:
                ack = k
                acv = self.json_join_content[k]
                break
        self.instancier = Instancier(
            self.foreign_table,
            None,
            parsed_table=self.parsed_table,
            json_inst_dict={
                "VODML": {
                    "MODELS":{},
                    "GLOBALS":{},
                    "TEMPLATES": {
                        "OtherResults": {
                            "@table_ref": self.foreign_table,
                            "root": [
                                {
                                    "ARRAY": {
                                        "FILTER": {
                                            "@ref": self.foreign_key,
                                            "@value": -1
                                            },
                                        ack: acv
                                        },
                                    }
                                ]
                            }
                        }
                    }
                }
            )
        self.instancier.resolve_refs_and_values(resolve_refs=False)
        self.instancier.map_columns()

        
    def get_subset_instance(self, key_value):
        self.row_filter = RowFilter({
                "@ref": self.foreign_key,
                "@value": key_value
                })
        self.table_iterator = TableIterator(
                                "join",
                                self.table_votable, 
                                self.json_join_content,
                                self.column_mapping,
                                self.row_filter
                                )
        
    