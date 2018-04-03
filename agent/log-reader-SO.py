# Windows Logs - System, Application and Security : > :
# https://docs.microsoft.com/en-us/previous-versions/windows/it-pro/windows-server-2008-R2-and-2008/cc722404%28v%3dws.10%29

# StackOverflow question  : > :  https://stackoverflow.com/questions/11219213/read-specific-windows-event-log-event
# Docs and possible improvent (an application which looks for all events for a server since a certain time) : > :  http://timgolden.me.uk/pywin32-docs/Windows_NT_Eventlog.html

# win32evtlog  : > :  https://github.com/wuxc/pywin32doc/blob/master/md/win32evtlog.md
import win32evtlog  # requires pywin32 pre-installed (pywin32: https://github.com/mhammond/pywin32 )


def main():
    server = 'localhost'  # name of the target computer to get event logs
    logtype = 'Security'  # 'Application' # 'Security' # 'System'
    hand = win32evtlog.OpenEventLog(server, logtype)
    flags = win32evtlog.EVENTLOG_FORWARDS_READ | win32evtlog.EVENTLOG_SEQUENTIAL_READ  # change EVENTLOG_FORWARDS_READ to EVENTLOG_BACKWARDS_READ in order to get the Events listed from the most recent one down to the first one ever logged
    total = win32evtlog.GetNumberOfEventLogRecords(hand)

    event_type_dict = {1: '1-Error', 2: 'Warsning', 4: '4-Information', 8: '8-Audit success', 16: '16-Audit failure'}

    while True:
        events = win32evtlog.ReadEventLog(hand, flags, 0)
        if events:
            for event in events:  # event attributes  : > :  https://github.com/wuxc/pywin32doc/blob/master/md/PyEventLogRecord.md
                # print('Sid', event.Sid)  # SecurityIDentifiers  : > :  https://msdn.microsoft.com/en-us/library/windows/desktop/aa379649%28v=vs.85%29.aspx
                print('Event Category:', event.EventCategory)
                print('Time Generated:', event.TimeGenerated)
                print('Source Name:', event.SourceName)  # name of the software that logs the event
                print('Event ID:', event.EventID)
                print('Event Type:', event_type_dict[event.EventType])
                data = event.StringInserts
                if data:
                    print('Event Data:')
                    for msg in data:
                        print(msg)
                print()


if __name__ == '__main__':
    main()
