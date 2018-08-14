# https://stackoverflow.com/questions/11908919/python-parsing-log-file-to-extract-events-in-real-time
# http://www.dabeaz.com/generators/follow.py

from datetime import datetime
import time
import os


month_dict = {
        'Jan': '01'', 'Feb': '02', 'Mar': '03', 'Apr': '04', 'May': '05', 'Jun': '06',
        'Jul': '07', 'Aug': '08', 'Sep': '09', 'Oct': '10', 'Nov': '11', 'Dec': '12'
                }

warning_words = ['warning', 'warn', 'reject', 'rejected']
error_words = ['error', 'err', 'bad']

current_year = datetime.now().year


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
            time.ctime()
            continue
        yield line


def decide_severity(log_text):
    for warning_word in warning_words:
        if log_text.find(warning_word):
            return 'WARNING'
    for error_word in error_words:
        if log_text.find(error_word):
            return 'ERROR'


#TODO
#def get_service(service_id_word):

#TODO
#def get_id(service_id_word):


def process_line(line):
    
    print line

    #SysLog format
        
    #Aug 14 14:51:36 osboxes dbus[502]: [system] Rejected send message, 7 matched rules; type='method_return', sender=':1.15' (uid=0 pid=1101 comm='/usr/sbin/dnsmasq --no-resolv --keep-in-foreground') interface='(unset)' member='(unset)' error name='(unset)' requested_reply='0' destination=':1.6' (uid=0 pid=939 comm='NetworkManager ')

    #Aug 12 11:17:01 osboxes CRON[3438]: pam_unix(cron:session): session opened for user root by (uid=0)
    #Aug 12 11:17:01 osboxes CRON[3438]: pam_unix(cron:session): session closed for user root

    #29-06-2018 17:08:15 	F4:4D:45:7D:36:FD 	student-service : 	INFO - [1530284895058] Logged out successfully
    #29-06-2018 17:08:17 	F4:4D:45:7D:36:FD 	student-service : 	CRITICAL - [1530284897058] Non-user has tried to sign up for an exam!

    words = line.split()

    timestamp = '{day}-{month}-{year} {time}'.format(day=words[1], month=month_dict[words[0]], year=current_year, time=words[2])
    mac_address = get_mac_address()
    #system name = words[3] (osboxes)
    #TODO
    #service = get_service(words[4])
    #severity = decide_severity(log_text.lower())
    #id = get_id(words[4])
    
    print '    Timestamp:'
    print '    ' + timestamp
    print '    MAC Address:'
    print '    ' + mac_address
    print '    -------------------------------------'

    #~ syslog_message = '{timestamp} \t{mac_address} \t{service} : \t{severity} - [{id}] {message}'.format(
            #~ timestamp=event.TimeGenerated.Format('%d-%m-%Y %H:%M:%S'),
            #~ mac_address=get_mac_address(),
            #~ service=event.SourceName,
            #~ severity=event_types[event.EventType],
            #~ id=event.EventID,
            #~ message=win32evtlogutil.SafeFormatMessage(event, log_type)
        #~ )


def main():
    configuration = read_config("config.json")
    logfile = open('/var/log/auth.log', 'r')
    #logfile = open('/tmp/test_log', 'r')
    loglines = follow(logfile)
    for line in loglines:
        process_line(line)


if __name__ == '__main__':
    main()


