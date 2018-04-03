# tutorial  : > :  https://www.youtube.com/watch?v=g8nQ90Hk328
import logging
import time
from random import randint
from uuid import getnode as get_mac


def get_app():
    app_list = ['student-service', 'professor-service', 'referent-service']
    return app_list[randint(0, 2)]


def get_mac_address():
    mac = get_mac()
    return ':'.join(("%012X" % mac)[i:i + 2] for i in range(0, 12, 2))


def get_message():
    username_list = ['username1', 'drstevanovic', 'filip.savic', 'coa995']
    messages = [
        '[{}] User with username: {} has logged in.',
        '[{}] User with username: {} has logged out.',
        '[{}] User with username: {} failed to log in.'
    ]
    return messages[randint(0, len(messages) - 1)].format(int(round(time.time() * 1000)),
                                                          username_list[randint(0, len(username_list) - 1)])


def main():
    #     timestamp
    #     mac address
    #     app
    #     severity

    log_format = "%(asctime)s \t" + get_mac_address() + " " + get_app() + " : \t%(levelname)s - %(message)s"
    logging.basicConfig(
        filename="generated-logs/socratica-log.log",
        datefmt='%d-%m-%Y %H:%M:%S',
        level=logging.DEBUG,
        format=log_format,
        filemode='a')

    logger = logging.getLogger()  # docs  : > :  https://docs.python.org/3/library/logging.html

    while True:
        logger.info(get_message())
        time.sleep(2)


if __name__ == '__main__':
    main()
