'''
Script used to test the output of a test run. Takes as input the number of
lines in the test file. Will make sure every message is received only once and
will print out messages that were received more or less than once.
'''

import sys

def main():
    if len(sys.argv) < 2:
        print 'Usage: python checkOutput.py <number_of_lines>'
        exit(1)

    with open('output.out') as f:
        lines = f.readlines()
    ids = map(lambda x: int(x[x.find(' ')+1:x.find('a',x.find(' '))]), lines)
    ids.sort()
    correct = range(0, int(sys.argv[1]))
    success = True
    wrong = []
    for i in correct:
        if ids.count(i) != 1:
            wrong.append((i, ids.count(i)))
            success = False
    if not success:
        print 'Failed: ' + str(wrong)
    else:
        print 'Success!'
    # print ids

if __name__ == '__main__':
    main()
