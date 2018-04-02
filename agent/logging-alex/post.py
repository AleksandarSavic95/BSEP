import requests

SERVER_ADDRESS = 'localhost'
SERVER_PORT = 8080 # 8765
API_PATH = '/api/reports'

url = 'http://{}:{}{}'.format(SERVER_ADDRESS, SERVER_PORT, API_PATH)

// data = '{ "location":"192.16osam", "logs":["<34>1 2003-10-11T22:14:15.003Z machine.exm.yu su - ID47 - BOM su root failed on XYZ"], "timestamp": 1522671129 }'

# TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP
#   Koristiti '''MULTI-LINE STRINGOVE''' za Json data !   TIP
#   Koristiti '''MULTI-LINE STRINGOVE''' za Json data !   TIP
#   Koristiti '''MULTI-LINE STRINGOVE''' za Json data !   TIP
# TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP TIP
data = '{ "log":"<34>1 2003-10-11T22:14:15.003Z machine.exm.yu su - ID47 - BOM su root failed on XYZ" }'

response = requests.post(url, json=data)

print('status and text of response: ', response.status_code, response.text)