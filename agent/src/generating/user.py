import logging
import random
import time
from uuid import getnode as get_mac


class State:
    def __init__(self, machine):
        self.machine = machine

    def log_in(self, username, password):
        success = log_in_to_server(username, password)
        if success:
            print('INFO - Logged in successfully')
            log('info', 'User {} logged in successfully'.format(username))
            self.machine.set_state(LoggedIn(self.machine))
            self.machine.set_logged_in(True)
        else:
            print('WARNING - Failed to log in')
            log('warning', 'User {} failed to log in.'.format(username))

    def log_out(self):
        print('INFO - Logged out successfully')
        log('info', 'Logged out successfully')
        self.machine.set_state(LoggedOut(self.machine))
        self.machine.set_logged_in(False)

    def sign_up_for_exam(self):
        pass

    def check_financial_card(self):
        pass


class LoggedOut(State):
    def log_out(self):
        pass

    def sign_up_for_exam(self):
        print('CRITICAL - Non-user tries to sign up for an exam!')
        log('critical', 'Non-user has tried to sign up for an exam!')

    def check_financial_card(self):
        print('CRITICAL - Non-user tries to check financial card')
        log('critical', 'Non-user has tried to check financial card!')


class LoggedIn(State):
    def sign_up_for_exam(self):
        print('INFO - Signing up for an exam')
        log('info', 'Signing up for an exam')
        self.machine.set_state(RegisteringExam(self.machine))

    def check_financial_card(self):
        print('INFO - Checking financial card')
        log('info', 'Checking financial card')
        self.machine.set_state(CheckingFinancialCard(self.machine))


class RegisteringExam(State):
    def sign_up_for_exam(self):
        print('INFO - Signing up for an exam')
        log('info', 'Signing up for an exam')

    def check_financial_card(self):
        print('INFO - Checking financial card')
        log('info', 'Checking financial card')
        self.machine.set_state(CheckingFinancialCard(self.machine))


class CheckingFinancialCard(State):
    def sign_up_for_exam(self):
        print('INFO - Signing up for an exam')
        log('info', 'Signing up for an exam')
        self.machine.set_state(RegisteringExam(self.machine))


class User:
    def __init__(self):
        self.logged_in = False
        self.current_state = LoggedOut(self)

    def set_state(self, state):
        self.current_state = state

    def set_logged_in(self, logged_in):
        self.logged_in = logged_in

    def log_in(self, username, password):
        print('User::log_in')
        self.current_state.log_in(username, password)
        print(self.current_state.__class__.__name__)
        print('')

    def log_out(self):
        print('User::log_out')
        self.current_state.log_out()
        print(self.current_state.__class__.__name__)
        print('')

    def sign_up_for_exam(self):
        print('User::sign_up_for_exam')
        self.current_state.sign_up_for_exam()
        print(self.current_state.__class__.__name__)
        print('')

    def check_financial_card(self):
        print('User::check_financial_card')
        self.current_state.check_financial_card()
        print(self.current_state.__class__.__name__)
        print('')


def log_in_to_server(username, password):
    # simulates sending POST request to the server
    return random.random() < 0.55  # in 55% cases, logging will be successful


def get_mac_address():
    """
    Returns a string representing the MAC address of the device"""
    mac = get_mac()
    return ':'.join(("%012X" % mac)[i:i + 2] for i in range(0, 12, 2))


def log(log_type, message):
    log_format = "%(asctime)s \t" + get_mac_address() + " \t%(app)s : \t%(levelname)s - %(message)s"
    logging.basicConfig(
        filename="logs/student-service-logs.log",
        datefmt='%d-%m-%Y %H:%M:%S',
        level=logging.DEBUG,
        format=log_format,
        filemode='a')

    logger = logging.getLogger()  # docs  : > :  https://docs.python.org/3/library/logging.html
    write_with_severity = {'debug': logger.debug, 'info': logger.info, 'warning': logger.warning, 'error': logger.error,
                           'critical': logger.critical}

    timestamp = int(round(time.time() * 1000))
    message = '[{}] {}'.format(timestamp, message)
    write_with_severity[log_type](message, extra={'app': 'student-service'})
