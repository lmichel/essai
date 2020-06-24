'''
Created on 1 avr. 2020

@author: laurentmichel
'''

class RowFilter(object):
    '''
    classdocs
    '''


    def __init__(self, json_block):
        '''
        Constructor
        '''
        if "@name" in json_block.keys():
            self.name = json_block["@name"]   
        else:
            self.name = "NoName"
        self.value = json_block["@value"]        
        self.key = json_block["@ref"]
        self.col_number=-1
     
    def __repr__(self):
        return "Row filter {} key={} value={} col={}".format(self.name, self.key, self.value, self.col_number)
        
    def map_col_number(self, column_mapping):
        self.col_number = column_mapping.get_col_index_by_name(self.key)
    
    def row_match(self, row):
        if str(row[self.col_number]) == str(self.value):
            return True
        return False