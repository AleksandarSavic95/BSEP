# https://stackoverflow.com/questions/11908919/python-parsing-log-file-to-extract-events-in-real-time
# http://www.dabeaz.com/generators/follow.py

import json
import os

from datetime import datetime
import time

from requests_util import open_session, send_log

AUTH_ERROR_MSG = 'bad'

month_dict = {
        'Jan': '01', 'Feb': '02', 'Mar': '03', 'Apr': '04', 'May': '05', 'Jun': '06',
        'Jul': '07', 'Aug': '08', 'Sep': '09', 'Oct': '10', 'Nov': '11', 'Dec': '12'
        }

current_year = datetime.now().year

def findOccurrences(s, ch):
    return [i for i, letter in enumerate(s) if letter == ch]


def read_config(config_path):
    with open(config_path) as f:
        configuration = json.load(f)
    return configuration["OS"]


def get_mac_address(interface='eth0'):
    try:
        mac = open('/sys/class/net/'+interface+'/address').readline()
    except:
        mac = '00:00:00:00:00:00'
    
    return mac[0:17]


def follow(thefile):
    thefile.seek(0, os.SEEK_END)
    while True:
        line = thefile.readline()
        if not line:
            time.sleep(1)
            continue
        yield line


def get_service_and_id(service_id_word):
    ''' service_id_word examples: 'dbus[502]:'  or: 'pkexec:' '''
    print 'print service_id_word'
    print service_id_word
    bracket_index = service_id_word.find('[')
    
    if bracket_index != -1:
        service = service_id_word[ : bracket_index]
        id = service_id_word[bracket_index : -1]
    else:
        service = service_id_word[ : -1]
        id = None
    
    print 'print service, id'
    print service, id
    return service, id


def get_severity(log_message):
    if log_message.find('session opened for user') != -1 or log_message.find('session closed for user') != -1:
        return 'WARNING'
    if log_message.find(AUTH_ERROR_MSG) != -1 :
        return 'ERROR'
    return 'INFO'


    #Aug 14 14:51:36 osboxes dbus[502]: [system] Rejected send message, 7 matched rules; type='method_return', sender=':1.15' (uid=0 pid=1101 comm='/usr/sbin/dnsmasq --no-resolv --keep-in-foreground') interface='(unset)' member='(unset)' error name='(unset)' requested_reply='0' destination=':1.6' (uid=0 pid=939 comm='NetworkManager ')
    #Aug 14 15:39:48 osboxes pkexec: pam_unix(polkit-1:session): session opened for user root by (uid=1000)
    #Aug 14 15:39:48 osboxes pkexec[23562]: osboxes: Executing command [USER=root] [TTY=unknown] [CWD=/home/osboxes] [COMMAND=/usr/lib/update-notifier/package-system-locked]
    #Aug 12 11:17:01 osboxes CRON[3438]: pam_unix(cron:session): session closed for user root

    #29-06-2018 17:08:15 	F4:4D:45:7D:36:FD 	student-service : 	INFO - [1530284895058] Logged out successfully
    #29-06-2018 17:08:17 	F4:4D:45:7D:36:FD 	student-service : 	CRITICAL - [1530284897058] Non-user has tried to sign up for an exam!
def format_to_syslog(line, severities):
    ''' Returns received log line in syslog format. '''
    print line

# split on 5th whitespace(occurrences[4]); split [0] into log attrs and [1] is log message
    log_attributes_end_index = findOccurrences(line, ' ')[4]  # log message begins after the 5th whitespace character
    log_attributes_line = line[ : log_attributes_end_index]  #date, time, MAC Address, severity and id
    message = line[log_attributes_end_index : ]

    print ''
    print 'print log_attributes_line and log_text'
    print log_attributes_line
    print message

    log_attributes = log_attributes_line.split()

    timestamp = '{day}-{month}-{year} {time}'.format(day=log_attributes[1], month=month_dict[log_attributes[0]], year=current_year, time=log_attributes[2])
    mac_address = get_mac_address()
    #system name = log_attributes[3] (osboxes)
    service, id = get_service_and_id(log_attributes[4])
    
    severity = get_severity(message)
    
    print '\n    Timestamp:' + timestamp
    print '    MAC Address:' + mac_address
    print '    Service' + service
    print '    id' + id
    print '    severity' + severity
    print '    -------------------------------------'

    if not severity in severities:
        return None
    
    syslog_message = "{timestamp} \t{mac_address} \t{service} : \t{severity} - {id} {message}".format(
        timestamp=timestamp,
        mac_address=mac_address,
        service=service,
        severity=severity,
        id=id,
        message=message
    )
    
    return syslog_message


def main():
    config = read_config("linux_config.json")
    auth_config = config['log_files'][0]
    session = open_session()
    
    #could/should read all paths from config file (todo)
    logfile = open(auth_config['path'], 'r')
    
    #logfile = open('/tmp/test_log', 'r')
    loglines = follow(logfile)
    for line in loglines:
        syslog_formatted_log = format_to_syslog(line, auth_config['severities'])
        send_log(session, syslog_formatted_log)
        print 'sent syslog formatted log'
        print syslog_formatted_log
        print 'reading new line...\n'


if __name__ == '__main__':
    main()


