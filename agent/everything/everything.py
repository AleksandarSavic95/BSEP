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
        directory = item[1] # item[0] je samo broj iz config fajla  (1,2,3..)
        print('files (folders are skipped) in: ' + directory)
        for dir_or_file in os.listdir(directory):
            if not os.path.isdir(dir_or_file):
                print('\t' + dir_or_file)
                log_files.append(os.path.join(directory, dir_or_file))
    
    siem_address = config.get('SIEM data', 'address')
    print('SIEM center address: ' + siem_address)
    
    regexps = [item[1] for item in config.items('Regex filters')]
    # for item in config.items('Regex filters'):
    #     regexps.append(item[1])
    
    # make a dictionary
    config_dict = {'logs': log_files, 'siem': siem_address, 'regexps': regexps}
    return log_files, siem_address, regexps # OR: config_dict

import time
import re

def follow(file):
    file.seek(0,2) # Go to the end of the file
    while True:
        line = file.readline()
        if not line:
            time.sleep(0.5) # Sleep briefly
            continue
        yield line

import requests

SERVER_ADDRESS = 'localhost'
SERVER_PORT = 8765
API_PATH = '/api/reports'

url = 'http://{}:{}{}'.format(SERVER_ADDRESS, SERVER_PORT, API_PATH)

def send_log(log_line):
    data = '{ "log": "' + log_line + '"}'
    response = requests.post(url, json=data)
    print('status and text of response: ', response.status_code, response.text)

if __name__ == '__main__':
    log_files, siem_address, regexps = read_config('config.ini')
    print('Config read. Opening log files...')

    files = []
    for f in log_files:
        print('opening file ' + f + ' ...')
        files.append(open(f))

    # za svaki otvoreni fajl
    # for f in files:
    lines = follow(files[0]) # za pocetak samo prvi fajl
    for line in lines:
        for regex in regexps:
            print('tryting to match regex: ' + regex)
            if re.match(regex, line):
                print('matched ' + regex + ' with ' + line)
                send_log(line) # not tested !
                break # don't match any other regexps

    # !!!
    # for f in files:
    #     f.close()