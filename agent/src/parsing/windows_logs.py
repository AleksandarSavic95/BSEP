import json
import os
import socket
import sys
import win32evtlog

import win32con
import win32evtlogutil

from src.parsing.requests_util import open_session, send_log

ip = socket.gethostbyname(socket.gethostname())
event_types = {win32con.EVENTLOG_AUDIT_FAILURE: 'ERROR',
               win32con.EVENTLOG_AUDIT_SUCCESS: 'INFO',
               win32con.EVENTLOG_INFORMATION_TYPE: 'INFO',
               win32con.EVENTLOG_WARNING_TYPE: 'WARNING',
               win32con.EVENTLOG_ERROR_TYPE: 'ERROR'}


def read_config(config_path):
    with open(config_path) as f:
        configuration = json.load(f)
    return configuration["OS"]


def get_mac_address():
    if sys.platform == 'win32':
        for line in os.popen("ipconfig /all"):
            if line.lstrip().startswith('Physical Address'):
                mac = line.split(':')[1].strip().replace('-', ':')
                break
    return mac


def send_event_logs(log_type="Application", severities=[], start_from=-1, session=None):
    read_flags = win32evtlog.EVENTLOG_SEEK_READ | win32evtlog.EVENTLOG_FORWARDS_READ

    handle = win32evtlog.OpenEventLog(None, log_type)
    total = win32evtlog.GetNumberOfEventLogRecords(handle)

    if start_from < 0:
        start_from = total
    last = start_from

    events = win32evtlog.ReadEventLog(handle, read_flags, start_from)
    for event in events:
        if not event_types[event.EventType] in severities:
            continue
        syslog_message = "{timestamp} \t{mac_address} \t{service} : \t{severity} - [{id}] {message}".format(
            timestamp=event.TimeGenerated.Format("%d-%m-%Y %H:%M:%S"),
            mac_address=get_mac_address(),
            service=event.SourceName,
            severity=event_types[event.EventType],
            id=event.EventID,
            message=win32evtlogutil.SafeFormatMessage(event, log_type)
        )

        print("WINDOWS: ", log_type, ", ", event.RecordNumber)
        print(syslog_message)
        print()
        send_log(session, syslog_message)

        last = event.RecordNumber

    return last


def main():
    configuration = read_config("config.json")

    session = open_session()

    last_records = {}
    for log_type in configuration["log_types"]:
        last_records[log_type["name"]] = -1

    while True:
        for log_type in configuration["log_types"]:
            print(log_type["name"])
            print(log_type["severities"])

            last_records[log_type["name"]] = send_event_logs(log_type=log_type["name"],
                                                             severities=log_type["severities"],
                                                             start_from=last_records[log_type["name"]],
                                                             session=session)


if __name__ == '__main__':
    main()
