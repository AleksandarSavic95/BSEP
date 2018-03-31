import logging
import logging.config

def main():
    # A logger that writes to a file.
    logging.basicConfig(level=logging.DEBUG,
                        format='%(asctime)s %(name)-12s %(levelname)-8s %(message)s',
                        datefmt='%m-%d %H:%M',
                        filename='generated-logs/prechecks.log',
                        filemode='w')

    # A handler that writes to the console.
    console = logging.StreamHandler()
    console.setLevel(logging.DEBUG)

    # Sets a format which is simpler for console use.
    formatter = logging.Formatter('%(name)-12s: %(levelname)-8s %(message)s')

    # Tell the handler to use this format.
    console.setFormatter(formatter)

    # Adds the handler to the root logger.
    logging.getLogger('').addHandler(console)

    logger = logging.getLogger()

    logger.debug("other debug sample")

if __name__=='__main__':
    main()