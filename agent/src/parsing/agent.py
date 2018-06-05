import re
import time
import json
from os import listdir, environ
from os.path import join, isfile, abspath

from requests import Session
from requests_pkcs12 import Pkcs12Adapter

SERVER_ADDRESS = 'localhost'
SERVER_PORT = 8765
API_PATH = '/api/reports'

ROOT_CERT_PATH = 'rootCA.pem'
KEYSTORE_PATH = 'agentKeyStore.p12'

FDA_SIEM_CERT_PASS = 'FDA_SIEM_CERT_PASS'

MY_HEADERS = {
    'Content-Type': 'application/json'
}

url = 'https://{}:{}{}'.format(SERVER_ADDRESS, SERVER_PORT, API_PATH)


def send_log(session, log_line):
    data = {'log': log_line}
    response = session.post(url, json=data, verify=ROOT_CERT_PATH)
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
        print('\n\tERROR - Invalid path: ' + path)
        return False
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
        new_line = file.readline()
        if not new_line:
            time.sleep(0.5)  # Sleep briefly # TODO: sleep longer???
            continue
        yield new_line


if __name__ == '__main__':
    config = read_config('config.json')
    print('Config read. Opening log files...')

    print('FDA_SIEM_CERT_PASS: ' + environ[FDA_SIEM_CERT_PASS])

    session = Session()
    # every request starting with `server_url` will use the Pkcs12Adapter
    # http://docs.python-requests.org/en/master/user/advanced/#transport-adapters
    pkcs12_adapter = Pkcs12Adapter(pkcs12_filename=KEYSTORE_PATH, pkcs12_password=environ[FDA_SIEM_CERT_PASS])
    session.mount(url, pkcs12_adapter)
    session.headers.update(MY_HEADERS)

    for directory in config['Directories']:
        opened_log_files = read_dir(directory['path'])
        if opened_log_files:
            directory['patterns'] = [re.compile(reg) for reg in directory['regexps']]
            print('Opened {} log files with {} patterns in dir {}'
                  .format(len(opened_log_files), len(directory['patterns']), directory['path']))

            # # for opened_log_file in opened_log_files: # # TODO: CITATI SVE FAJLOVE!!!
            opened_log_file = opened_log_files[0]  # za pocetak samo prvi fajl
            lines = follow(opened_log_file)
            for line in lines:
                print(line)
                for pattern in directory['patterns']:
                    print('tryting to match regex: ' + pattern.pattern)
                    if bool(re.search(pattern, line)):
                        print('matched\n\t' + pattern.pattern + '\nwith\n\t' + line)
                        send_log(session, line)  # not tested !
                        break  # don't match any other regexps
                    print()
                print('Waiting for a new log')

    print('END?')
    # !!!
    # for f in files:
    #     f.close()
