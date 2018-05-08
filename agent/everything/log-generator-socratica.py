# tutorial  : > :  https://www.youtube.com/watch?v=g8nQ90Hk328
import logging
import time
from random import randint
from uuid import getnode as get_mac


def get_app():
    """
    Returns a string representing the app which generated the log (random)."""
    app_list = ['student-service', 'professor-service', 'referent-service']
    return app_list[randint(0, len(app_list) - 1)]


def get_mac_address():
    """
    Returns a string representing the MAC address of the device"""
    mac = get_mac()
    return ':'.join(("%012X" % mac)[i:i + 2] for i in range(0, 12, 2))


def get_message():
    """
    Returns a string representing a log message with randomly generated
    text and user's username.
    """
    username_list = ['username1', 'drstevanovic', 'filip.savic', 'coa995']
    messages = [
        '[{}] User with username: {} has logged in.',
        '[{}] User with username: {} has logged out.',
        '[{}] User with username: {} failed to log in.'
    ]
    return messages[randint(0, len(messages) - 1)].format(int(round(time.time() * 1000)),
                                                          username_list[randint(0, len(username_list) - 1)])


def main():
    # Syslog format: timestamp, mac address, app, severity, message
    log_format = "%(asctime)s \t" + get_mac_address() + " \t%(app)s : \t%(levelname)s - %(message)s"
    logging.basicConfig(
        filename="logs/socratica-log.log",
        datefmt='%d-%m-%Y %H:%M:%S',
        level=logging.DEBUG,
        format=log_format,
        filemode='a')

    logger = logging.getLogger()  # docs  : > :  https://docs.python.org/3/library/logging.html

    severity_func_list = [logger.debug, logger.info, logger.warning, logger.error, logger.critical]

    while True:
        severity_func = severity_func_list[randint(0, 4)]  # random logger severity level

        # adding a custom variable to logging format (through extra param)
        # https://stackoverflow.com/a/17558757/4345461
        severity_func(get_message(), extra={'app': get_app()})

        time.sleep(2)


if __name__ == '__main__':
    main()
