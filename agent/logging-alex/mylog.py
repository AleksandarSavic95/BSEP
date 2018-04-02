from logging.handlers import SysLogHandler
import logging

logger = logging.getLogger()
logger.addHandler(SysLogHandler(('localhost', 514), SysLogHandler.LOG_LOCAL5, 1))
logger.addHandler(logging.FileHandler("log.txt")) # D:\Radim\BSEP\BSEPproject\logging

logging.warn("Hello world")