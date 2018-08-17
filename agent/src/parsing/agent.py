import json
import re
import time
from os import listdir, environ
from os.path import join, isfile, abspath

from threading import Thread
import platform

from requests import Session
from requests_pkcs12 import Pkcs12Adapter

SERVER_ADDRESS = '192.168.1.10'
SERVER_PORT = 8765
API_PATH = '/api/reports'

ROOT_CERT_PATH = 'rootCA.pem'
KEYSTORE_PATH = 'agentKeyStore.p12'

FDA_SIEM_CERT_PASS = 'FDA_SIEM_CERT_PASS'

MY_HEADERS = {
    'Content-Type': 'application/json'
}

url = 'https://{}:{}{}'.format(SERVER_ADDRESS, SERVER_PORT, API_PATH)


import application_logs


if __name__ == '__main__':
    print platform.system().upper()
    if platform.system().upper()=='WINDOWS':
        import windows_logs
        thread1 = Thread(target=windows_logs.main)
    if platform.system().upper()=='LINUX':
        import linux_logs
        thread1 = Thread(target=linux_logs.main)
    thread2 = Thread(target=application_logs.main)

    thread1.start()
    thread2.start()
