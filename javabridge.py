#do some path manipulation for the stdlib
import sys
sys.path.append(__file__[:__file__.rindex('/')] + "/pylib")
from javax.swing import JLabel, JPanel, JButton
import tools
import cPickle as pickle
import mict.client.ClientState
def get_tools(clientstate= None) :
    tools_instances = []
    for tool in tools.tools :
        tools_instances.append(tool(clientstate))
    return tools_instances

def reload_tools(clientstate=None) :
    global tools
    tools = reload(tools)
    return get_tools(clientstate)


PICKLE_FILE = 'tools.pickle'

active_tools = []

def get_cached_tools() :
    up = pickle.Unpickler(open(PICKLE_FILE))
    tools = []
    try:
        while True :
            tools.append(up.load())
    except EOFError:
        pass #this should happen
    return tools
def get_client_tools(clientState = None):
    return [tool(clientState) for tool in active_tools]
def get_needed_tools(serverTools):
    all_tools = [(x[0],x[1]) for x in tool.split(':',1) for tool in serverTools.split(';')]
    needed_tools = []
    hashed_tools = {}
    for tool in get_cached_tools() :
        hashed_tools[(tool.toolID(None),hash(tool))] = tool
    global active_tools
    for tool in all_tools:
        in_cache = hashed_tools.get(tool, None)
        if not in_cache :
            needed_tools.add(tool[0]) #this is the toolID
        else :
            active_tools.append(in_cache)
    return needed_tools
def add_tool(pickleStream, clientState=None):
    global active_tools
    tool = pickle.loads(pickleStream)
    pickle.dump(tool, open(PICKLE_FILE,'a'))
    active_tools.append(tool)
    return tool(clientState)


def serialize_tool(toolID):
    '''return a pickled form of the tool with the given toolID. This should be used on the server side'''
    for tool in tools.tools :
        if tool.getToolID() == toolID :
            return pickle.dumps(tool)
    return ''