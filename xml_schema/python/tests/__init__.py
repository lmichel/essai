import sys, os
from utils.logger_setup import LoggerSetup


path = os.path.dirname(os.path.realpath(__file__)) 

if path not in sys.path:
    sys.path.append(path + "/../")


logger = LoggerSetup.get_logger()
LoggerSetup.set_debug_level()

# make sure to know where we are to avoid issue with relative paths
os.chdir(os.path.dirname(os.path.realpath(__file__)))

#Config.__read_config__(config_file)
logger.info("test package intialized")