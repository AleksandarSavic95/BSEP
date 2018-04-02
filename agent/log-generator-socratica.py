# tutorial  : > :  https://www.youtube.com/watch?v=g8nQ90Hk328
import logging
from uuid import getnode as get_mac


def get_app():
    from random import randint
    app_list = ['student-service', 'professor-service', 'referent-service']
    return app_list[randint(0, 2)]

def main():
    #     timestamp
    #     mac address
    #     app
    #     severity
    mac = get_mac()
    mac = ':'.join(("%012X" % mac)[i:i + 2] for i in range(0, 12, 2))

    log_format = "%(asctime)s - " + mac + " : " + get_app() + " :  %(levelname)s - %(message)s"
    logging.basicConfig(
        filename="generated-logs/socratica-log.log",
        datefmt='%d-%m-%Y %H:%M:%S',
        level=logging.DEBUG,
        format=log_format,
        filemode='a')

    logger = logging.getLogger()  # docs  : > :  https://docs.python.org/3/library/logging.html

    logger.debug("this is a harmless debug message.")
    logger.info("this is a informing message")
    logger.warning("this is a warning message")
    logger.error("this is an error message")
    logger.critical("this is a critical message")

if __name__ == '__main__':
    main()
