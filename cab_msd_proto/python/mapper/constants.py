'''
Created on 15 avr. 2020

@author: laurentmichel
'''
from collections import namedtuple


ParamTemplates = namedtuple('ParamTemplates', ['POSITION'])
PARAM_TEMPLATES = ParamTemplates(
    """
    <INSTANCE dmrole="cab_msd:Source.parameters" dmtype="cab_msd:Parameter">
    <VALUE dmrole="cab_msd:Parameter.semantic" dmtype="ivoa:string" value="measurememt"/>
    <VALUE dmrole="cab_msd:Parameter.ucd" dmtype="ivoa:string" value="a.b.c"/>
    </INSTANCE>
    """
    )
