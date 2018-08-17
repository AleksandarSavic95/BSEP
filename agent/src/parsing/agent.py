import platform
from threading import Thread

import application_logs

if __name__ == '__main__':
    print(platform.system().upper())
    if platform.system().upper() == 'WINDOWS':
        pass
    #     thread1 = Thread(target=windows_logs.main)
    if platform.system().upper() == 'LINUX':
        pass
    #     thread1 = Thread(target=linux_logs.main)
    # thread1.start()

    thread2 = Thread(target=application_logs.main)
    thread2.start()
