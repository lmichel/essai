'''
Created on 26 mars 2020

@author: laurentmichel
'''
from inst_builder.instancier import Instancier

class InstanceBuilderRunner:
    def __init__(self, votable_path, instance_path):
        self.votable_path = votable_path
        self.instance_path = instance_path
        self._instancier = Instancier(self.votable_path
                                  , self.instance_path)

    @property
    def instance(self):
        return self._instancier
    
    def run(self):
        self._instancier.set_element_values()
        self._instancier.set_array_values()
        self._instancier.map_columns()
    

