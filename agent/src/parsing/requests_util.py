import json
from os import environ

from requests import Session
from requests_pkcs12 import Pkcs12Adapter


def read_config(config_path):
    with open(config_path) as f:
        conf = json.load(f)
    return conf["SIEM"]


configuration = read_config("linux_config.json")

HTTP_PROTOCOL = configuration["protocol"]
SERVER_ADDRESS = configuration["host_name"]
SERVER_PORT = configuration["port"]
API_PATH = '/api/reports'

ROOT_CERT_PATH = 'rootCA.pem'
KEYSTORE_PATH = 'agentKeyStore.p12'

FDA_SIEM_CERT_PASS = 'FDA_SIEM_CERT_PASS'

MY_HEADERS = {
    'Content-Type': 'application/json'
}

url = '{}://{}:{}{}'.format(HTTP_PROTOCOL, SERVER_ADDRESS, SERVER_PORT, API_PATH)


def open_session():
    session = Session()
    # every request starting with `server_url` will use the Pkcs12Adapter
    # http://docs.python-requests.org/en/master/user/advanced/#transport-adapters
    pkcs12_adapter = Pkcs12Adapter(pkcs12_filename=KEYSTORE_PATH, pkcs12_password=environ[FDA_SIEM_CERT_PASS])
    session.mount(url, pkcs12_adapter)
    session.headers.update(MY_HEADERS)
    return session


def send_log(session, log_line):
    data = {'log': log_line}
    try:
        response = session.post(url, json=data, verify=ROOT_CERT_PATH)
        print('status and text of response: ', response.status_code, response.text)
    except:

        print("ConnectionError")


if __name__ == '__main__':
    url = "https://172.20.10.3:8765/api/logs/all?page=0&size=10"
    print(url)

    data = {
        'log': "26-06-2018 00:37:02 	34:23:87:02:53:AE 	student-service : 	CRITICAL - [1529966222737] Non-user has tried to sign up for an exam!"
    }

    session = open_session()
    response = session.post(url,
                            json=data,
                            verify=ROOT_CERT_PATH)
