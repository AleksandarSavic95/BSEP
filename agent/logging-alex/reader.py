import time
import re

def follow(syslog_file):
    syslog_file.seek(0,2) # Go to the end of the file
    while True:
        line = syslog_file.readline()
        if not line:
            time.sleep(0.5) # Sleep briefly
            continue
        yield line


if __name__ == '__main__':
	f = open('log.txt')
	lines = follow(f)
	regexes = ['^.*?WARNING.*$', '^.*?ERROR.*$', '^.*?CRITICAL.*$']
	for i, line in enumerate(lines):
		for regex in regexes:
			if re.match(regex, line):
				print('matched ' + regex + ' with ' + line)
				break # stop going through 
		print('line ' + str(i))
