#do some path manipulation for the stdlib
import sys
if '/' in __file__ :
    sys.path.append(__file__[:__file__.rindex('/')] + "/pylib")
else :
    sys.path.append('pylib')
from javax.swing import JLabel, JPanel, JButton
import tools
from java.io import ObjectOutputStream, BufferedReader, InputStreamReader, PrintWriter, PipedInputStream, PipedOutputStream, BufferedOutputStream, BufferedInputStream, FileOutputStream
from org.python.util import PythonObjectInputStream as ObjectInputStream
import cStringIO
import cPickle as pickle
import mict.client.ClientState
import types
import mict.tools
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

_excluded_methods = 'class','classDictInit', 'equals', 'finalize', 'getClass', 'hashCode', 'notify', 'notifyAll', 'wait', 'toString', 'clone'
def serialize_tool(toolID):
    s_tool = None
    for tool in tools.tools :
        if tool().getToolID() == toolID :
            s_tool = tool
            break
    if s_tool == None :
        return ""
    class_dict = {}
    outStream = PipedOutputStream()
    inputStream = PipedInputStream(outStream)
    foutStream = FileOutputStream('test.oos')
    istream = BufferedReader(InputStreamReader(inputStream),10240)
    ostream = BufferedOutputStream(outStream)
    oout= ObjectOutputStream(ostream)
    for iname in dir(tool) :
    
        if not (iname.startswith('_') and iname != '__init__') and iname not in _excluded_methods :
            try :
                item = getattr(tool,iname)
                if isinstance(item, types.MethodType) :

                    oout.writeObject(item.im_func.func_code)
                    print "wrote object"
                    
                    ostream.flush()
                    print "flushed"
                    #now build the string
                    chrs = []
                    
                    while istream.ready() :
                        print "reading"
                        chrs.append(char(istream.read()))
                    print chrs
                    class_dict[iname + '.f'] = ''.join(chrs)
                else :
                    class_dict[iname] = item
            except AttributeError :
                pass
                #this gets triggered for properties
    return pickle.dumps(class_dict)
def unserialize_tool(tool_string): 
    print "deserializing"
    name, pickled = tool_string.split(';',1)
    marshal_dict = pickle.loads(pickled)
    print marshal_dict
    class_dict = {}
    inputStream = PipedInputStream()
    
   
    outStream = PipedOutputStream(inputStream)
    ostream = PrintWriter(outStream)
    inStream = BufferedInputStream(inputStream)
    inRead = BufferedReader(InputStreamReader(inputStream))
   #oin = ObjectInputStream(inputStream)
    print "streams written"
    oin= None
    print "preparing to extract"
    for iname in marshal_dict :
        print iname
        if not iname.endswith('.f') :
            class_dict[iname] = marshal_dict[iname]
        else :
            ostream.print(marshal_dict[iname])
            while inRead.ready() :
                print unichr(inRead.read()),
            print ''
                
         #   func = types.FunctionType(oin.readObject(),globals(), iname[:-2])
         #   class_dict[iname[:-2]] = func
    new_tool = type(name,(mict.tools.Tool,),class_dict)
if __name__ == "__main__" :
    x = serialize_tool('rect')
    y = unserialize_tool('rectangle;' + x)
    print dir(y)
    
