from ConfigParser import SafeConfigParser
import os.path
import sys
config = ConfigParser.SafeConfigParser()
import java.lang
if 'Windows' not in java.lang.System.getProperties()['os.name'] :
    PREFS_FILE = os.path.join(os.getenv("HOME"), '.mict')
else :
    PREFS_FILE = os.path.join(os.getenv("APPDATA"),"Local",'MICT')
config.read(PREFS_FILE)

def save_config() :
    f = open(PREFS_FILE,'w')
    config.write(f)
    f.close()


