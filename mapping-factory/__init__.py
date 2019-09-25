import logging
import sys

#from .task_scheduler import Scheduler
# create logger
logger = logging.getLogger('mapponig factory')
logger.setLevel(logging.DEBUG)

logging.basicConfig(stream=sys.stdout, level=logging.DEBUG,
                    format='%(asctime)s - [%(filename)s:%(lineno)s - %(funcName)20s() ] - %(levelname)s - %(message)s')
logger.info("scheduler package intialized")