from threading import Thread
import platform

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
