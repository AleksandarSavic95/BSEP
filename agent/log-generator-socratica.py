# tutorial  : > :  https://www.youtube.com/watch?v=g8nQ90Hk328
import logging


def main():
    log_format = "%(asctime)s  :  %(levelname)s  -  %(message)s"
    logging.basicConfig(
        filename="generated-logs/socratica-log.log",
        datefmt='%d-%m-%Y %H:%M:%S',
        level=logging.DEBUG,
        format=log_format,
        filemode='a')

    logger = logging.getLogger()

    logger.debug("this is a harmless debug message.")
    logger.info("this is a informing message")
    logger.warning("this is a warning message")
    logger.error("this is an error message")
    logger.critical("this is a critical message")

if __name__ == '__main__':
    main()
