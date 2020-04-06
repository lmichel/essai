'''
Created on 6 avr. 2020

@author: laurentmichel
'''
from inst_builder.instancier import Instancier
from astropy.coordinates import Galactic, ICRS, FK5, FK4

class AstropyWrapper(object):
    '''
    classdocs
    '''


    def __init__(self, instancier):
        '''
        Constructor
        '''
        self.instancier = instancier
        
    
    def get_space_frame(self, element):
            #ref_location = self.instancier.search_instance_by_role("coords:StdRefLocation.position", 
            #                                                root_element=ele)[0]['@value']
            #print(ref_location)   
            ref_frame = self.instancier.search_instance_by_role("coords:SpaceFrame.spaceRefFrame", 
                                                            root_element=element)[0]['@value'].upper()  
            if ref_frame == "Galactic":
                retour =  Galactic()
            else :
                ref_equinox = self.instancier.search_instance_by_role("coords:SpaceFrame.equinox", 
                                                            root_element=element)[0]['@value'] 
                if ref_frame == "ICRS":
                    if not ref_equinox :
                        retour = ICRS()
                    else:
                        retour = ICRS(equinox=ref_equinox)
                        
                elif ref_frame == "FK4":
                    if not ref_equinox :
                        retour = FK4()
                    else:
                        retour = FK4(equinox=ref_equinox)

                elif ref_frame == "FK5":
                    if not ref_equinox :
                        retour = FK5()
                    else:
                        retour = FK5(equinox=ref_equinox)

                else:
                    raise Exception ( "unsupported frame: " + ref_frame)    
            return retour                                       
                                                            
    