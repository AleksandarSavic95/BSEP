import re

logs = '''
03-04-2018 03:21:16 34:23:87:02:53:AE referent-service :  DEBUG - ID123456 this is a harmless debug message.
03-04-2018 03:21:16 34:23:87:02:53:AE referent-service :  INFO - ID123456 this is a informing message
03-04-2018 03:21:16 34:23:87:02:53:AE referent-service :  WARNING - ID123456 this is a warning message
03-04-2018 03:21:16 34:23:87:02:53:AE referent-service :  ERROR - ID123456 this is an error message
03-04-2018 03:21:16 34:23:87:02:53:AE referent-service :  CRITICAL - ID123456 this is a critical message
03-02-2018 21:55:28 8B:35:C3:6E:10:6C teachers-service:  WARNING - [ID123456] this is a warning    message.
03-04-2018 04:32:04 	34:23:87:02:53:AE referent-service : 	INFO - [1522722724083] User with username: filip.savic has logged out.

'''

# logs = '''02-04-2018 22:54:38 8B:35:C3:6E:10:6C  students-service :  CRITICAL - ID123456 this is  a !critical! message
# 03-04-2018 01:21:08 8B:35:C3:6E:10:6C wdw:  WARNING  - asd? +- '''

if __name__ == '__main__':

    timestamp_pattern = r'(\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2})'
    mac_address_pattern = r'(([0-9A-E]{2}:){5}[0-9A-E]{2})'
    service_name_pattern = r'([a-z0-9-]+)'
    severity_pattern = r'(DEBUG|INFO|WARNING|ERROR|CRITICAL)'
    msg_id_pattern = r'(\[[a-zA-Z0-9-_]+\])'
    msg_pattern = r'(([A-Za-z0-9-._:!]+|\s)+)'

    all_combined = r'\s+'.join([timestamp_pattern, mac_address_pattern, service_name_pattern]) + r'\s*:\s*' + \
                   severity_pattern + r'\s*-\s*' + msg_id_pattern + r'\s*[a-zA-Z0-9     .!?:_,]*'
    print('all_combined:\n' + all_combined)

    pattern = re.compile(all_combined)

    pattern = re.compile(r'(\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2})\s+(([0-9A-E]{2}:){5}[0-9A-E]{2})\s+([a-z0-9-]+)\s*:\s*'
                         r'(WARNING|ERROR|CRITICAL)')

    for match in pattern.finditer(logs):
        print(match, match.span())
        start, end = match.span()
        log = logs[start:end]
        print(log)
        print()
