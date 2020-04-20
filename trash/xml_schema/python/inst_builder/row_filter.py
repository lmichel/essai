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
        self.name = json_block["@name"]        
        self.value = json_block["@value"]        
        self.key = json_block["@key"]
        self.col_number=-1
        
    def map_col_number(self, column_mapping):
        self.col_number = column_mapping.get_col_index_by_name(self.key)
    
    def row_match(self, row):
        if str(row[self.col_number], 'utf-8') == self.value:
            return True
        return False