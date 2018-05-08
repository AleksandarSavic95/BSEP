import random
import time

from src.user import User

if __name__ == '__main__':
    users = ['fsavic', 'cojacasper', 'drstevanovic']

    all_actions = ['log in', 'log out', 'sign up for exam', 'check financial card']

    actions_for_users = ['sign up for exam', 'check financial card', 'sign up for exam', 'check financial card',
                         'log in', 'sign up for exam', 'check financial card', 'log out']
    user = User()

    while True:
        user.log_in(random.choice(users), '')

        if user.logged_in:
            num_of_actions = random.randint(3, 10)
            actions = actions_for_users
        else:
            num_of_actions = random.randint(1, 3)
            actions = all_actions

        for ac in range(num_of_actions):
            action = random.choice(actions)
            print(action)
            if action == 'log in':
                user.log_in(random.choice(users), '')
            elif action == 'log out':
                user.log_out()
            elif action == 'sign up for exam':
                user.sign_up_for_exam()
            elif action == 'check financial card':
                user.check_financial_card()
            pause = random.randint(1, 5)
            print('Pause {}'.format(pause))
            time.sleep(pause)

        user.log_out()
