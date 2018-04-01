# https://docs.python.org/2/library/configparser.html
import ConfigParser
import os

def read_config(config_path):
    """
    Reads the configuration file and returns:
     - list of directories [or log fieles?] to watch for (absolute paths),
     - web address of the SIEM center [+ more information],
     - list of regular expressions for filtering log entries,
     - ... ?
    """
    config = ConfigParser.ConfigParser()
    config.read(config_path)
    # config  .sections()  .options('section')  .items('section')
    log_files = []
    for item in config.items('directories'): # list of (key, value) pairs
        directory = item[1]
        # print('files (folders are skipped) in: ' + directory)
        for dir_or_file in os.listdir(directory):
            if os.path.isfile(dir_or_file):
                log_files.append(os.path.abspath(dir_or_file))
                # print('\t' + dir_or_file)
    
    siem_address = config.get('SIEM data', 'address')
    print('SIEM center address: ' + siem_address)
    
    regexps = []
    for item in config.items('Regex filters'):
        regexps.append(item[1])
    
    # make a dictionary
    config_dict = {'logs': log_files, 'siem': siem_address, 'regexps': regexps}
    return config_dict # OR: log_files, siem_address, regexps


config = read_config('config.ini')
