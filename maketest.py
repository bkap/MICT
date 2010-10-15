#!/usr/bin/env jython

from tools import tools

main_file = open('testsuite.java.template')

test = open('ToolTest.java','w')
test_template = open('testtool.java.template').read()
for line in main_file :
    test.write(line)

for tool in tools : 
    x = dict(tool=tool().getToolID())
    test.write(test_template % x)
test.write('}')
test.close()
