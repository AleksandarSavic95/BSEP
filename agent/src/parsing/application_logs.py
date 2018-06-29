import json
import re
import time
from os import listdir, environ
from os.path import join, isfile, abspath

from src.parsing.requests_util import send_log, open_session


def read_config(config_path):
    with open(config_path) as f:
        configuration = json.load(f)
    return configuration


def read_dir(path=''):
    """Opens every file in specified directory and returns the list of opened files."""
    try:
        dir_content = listdir(path)
    except FileNotFoundError:
        print('\n\tERROR - Invalid path: ' + path)
        return False
    print('Listing files in: ' + path)

    opened_log_files = []
    for dir_or_file in dir_content:
        # join gives same path as abspath? TODO: Move this to a variable and un-import abspath!
        if isfile(join(path, dir_or_file)):
            opened_log_files.append(open(join(path, dir_or_file)))
            print('\tFound a file: ' + dir_or_file + ' |ABS|: ' + abspath(dir_or_file))

    print('successfully read directory: ' + path)
    return opened_log_files


def follow(file):
    file.seek(0, 2)  # Go to the end of the file
    while True:
        new_line = file.readline()
        if not new_line:
            time.sleep(0.5)  # Sleep briefly # TODO: sleep longer???
            continue
        yield new_line


def main():
    print("Agent started")
    config = read_config('config.json')

    session = open_session()

    for directory in config['Directories']:
        opened_log_files = read_dir(directory['path'])
        if opened_log_files:
            directory['patterns'] = [re.compile(reg) for reg in directory['regexps']]
            print('Opened {} log files with {} patterns in dir {}'
                  .format(len(opened_log_files), len(directory['patterns']), directory['path']))

            # # for opened_log_file in opened_log_files: # # TODO: CITATI SVE FAJLOVE!!!
            opened_log_file = opened_log_files[0]  # za pocetak samo prvi fajl
            lines = follow(opened_log_file)
            for line in lines:
                print(line)
                for pattern in directory['patterns']:
                    print('tryting to match regex: ' + pattern.pattern)
                    if bool(re.search(pattern, line)):
                        print('matched\n\t' + pattern.pattern + '\nwith\n\t' + line)
                        send_log(session, line)  # not tested !
                        break  # don't match any other regexps
                    print()
                print('Waiting for a new log')

    print('END?')
    # !!!
    # for f in files:
    #     f.close()


if __name__ == '__main__':
    main()
