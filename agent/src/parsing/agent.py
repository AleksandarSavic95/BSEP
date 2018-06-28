from threading import Thread

from src.parsing import application_logs
from src.parsing import windows_logs

if __name__ == '__main__':
    thread1 = Thread(target=windows_logs.main)
    thread2 = Thread(target=application_logs.main)

    thread1.start()
    thread2.start()
