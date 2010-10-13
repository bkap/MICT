#do some path manipulation for the stdlib
import sys
sys.path.append(__file__[:__file__.rindex('/')] + "/pylib")
from javax.swing import JLabel, JPanel, JButton
import tools

import mict.client.ClientState
def get_tools(clientstate= None) :
    tools_instances = []
    for tool in tools_list :
        tools_instances.append(tool(clientstate))
    return tools_instances

tools_list = tools.tools
def reload_tools(clientstate=None) :
    global tools_list, tools
    tools = reload(tools)
    return get_tools(clientstate)

dispatcher = {"getTools":get_tools}
def bridge(caller, *args, **kwargs) :
   return dispatcher[caller](*args, **kwargs)
