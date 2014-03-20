'''
Script used to generate test files. Takes as an argument the number of lines
to be inserted into a test file. Each file contains a message of the format:
"^message #a?$". Where the number of a's is a random number between 0 and 5000.
'''

import random, sys

def main():
    if len(sys.argv) < 2:
        print 'Usage: python genTest.py <number_of_lines>'
        exit(1)

    lines = int(sys.argv[1])
    extraChars = ['a']*5000

    with open('test.txt', 'w') as f:
        for line in range(lines):
            extra = random.randint(0, 5000)
            f.write('message '+str(line)+''.join(extraChars[:extra])+'\n')

if __name__ == '__main__':
    main()
