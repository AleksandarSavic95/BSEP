import re
import time
import json
from os import listdir
from os.path import join, isfile, abspath

import requests

SERVER_ADDRESS = 'localhost'
SERVER_PORT = 8765
API_PATH = '/api/reports'

url = 'http://{}:{}{}'.format(SERVER_ADDRESS, SERVER_PORT, API_PATH)


def send_log(log_line):
    data = '{ "log": "' + log_line + '"}'
    response = requests.post(url, json=data)
    print('status and text of response: ', response.status_code, response.text)


def read_config(config_path):
    with open(config_path) as f:
        configuration = json.load(f)
    return configuration


def read_dir(path=''):
    """Opens every file in specified directory and returns the list of opened files."""
    try:
        dir_content = listdir(path)
    except FileNotFoundError:
        print('Invalid path: ' + path)
        return
    print('Listing files in: ' + path)

    opened_log_files = []
    for dir_or_file in dir_content:
        # join gives same path as abspath? TODO: Move this to a variable and un-import abspath!
        if isfile(join(path, dir_or_file)):
            opened_log_files.append(open(join(path, dir_or_file)))
            print('\tFound a file: ' + dir_or_file + ' |ABS|: ' + abspath(dir_or_file))

    print('successfully read directory: ' + path)
    return opened_log_files


def follow(file):
    file.seek(0, 2)  # Go to the end of the file
    while True:
        line = file.readline()
        if not line:
            time.sleep(0.5)  # Sleep briefly # TODO: sleep longer???
            continue
        yield line


if __name__ == '__main__':
    config = read_config('config.json')
    print('Config read. Opening log files...')

    for directory in config['Directories']:
        opened_log_files = read_dir(directory['path'])
        directory['patterns'] = [re.compile(reg) for reg in directory['regexps']]
        print('Opened {} log files with {} patterns in dir {}'
              .format(len(opened_log_files), len(directory['patterns']), directory['path']))

        # # for opened_log_file in opened_log_files:
        opened_log_file = opened_log_files[0]  # za pocetak samo prvi fajl
        lines = follow(opened_log_file)
        for line in lines:
            print(line)
            for pattern in directory['patterns']:
                print('tryting to match regex: ' + pattern.pattern)
                if bool(re.search(pattern, line)):
                    print('matched\n\t' + pattern.pattern + '\nwith\n\t' + line)
                    send_log(line)  # not tested !
                    break  # don't match any other regexps
                print()
            print('Waiting for a new log')

    print('END?')
    # !!!
    # for f in files:
    #     f.close()
