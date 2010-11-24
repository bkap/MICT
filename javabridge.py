#do some path manipulation for the stdlib
import sys
if '/' in __file__ :
	sys.path.append(__file__[:__file__.rindex('/')] + "/pylib")
else :
	sys.path.append('pylib')
from javax.swing import JLabel, JPanel, JButton
import tools
from java.awt.image import BufferedImage
from java.io import ObjectOutputStream, BufferedReader, InputStreamReader,\
PrintWriter, PipedInputStream, PipedOutputStream, BufferedOutputStream,\
BufferedInputStream, FileOutputStream, ObjectInputStream
import cStringIO
import cPickle as pickle
import mict.client.ClientState
import types
import mict.tools
import os.path
from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool

def get_tools(clientstate= None) :
	tools.reload_tools()
	tools_instances = []
	for tool in tools.tools :
		tools_instances.append(tool(clientstate))
	return tools_instances


active_tools = []

def get_client_tools(clientState = None):
	print "client tools: ",tools.tools
	return [tool(clientState) for tool in tools.tools]
def get_needed_tools(serverTools):
	print "getting needed tools"
	return tools.check_files(serverTools)
get_tool_files_and_hashes = tools.get_tool_files_and_hashes;
def get_server_tools(clientState = None) :
	tools.tools = tools._get_tools()
	return [tool(clientState) for tool in tools.tools]
def serialize_tool(toolID) :
	ser_tool = None
	for tool in tools :
		ser_tool = tool
		if ser_tool.getToolID() == toolID :
			break
	if ser_tool is None :
		return ""
	else :
		tool = tools.get_tool_definitions([toolID])
		if not tool :
			return ""
		else :
			return tool[0]
def deserialize_tool(phrase, clientState=None) :
	return [tool(clientState) for tool in tools.add_file(phrase)]
