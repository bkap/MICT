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
import misc
import os.path
from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool

def get_tools(clientstate= None) :
	tools.reload_tools()
	tools_instances = []
	for tool in tools.tools :
		tools_instances.append(tool(clientstate))
	return tools_instances

PICKLE_FILE = 'tools.pickle'

active_tools = []

def get_client_tools(clientState = None):
	return [tool(clientState) for tool in active_tools]
def get_needed_tools(serverTools):
	all_tools = [(x[0],x[1]) for x in tool.split(':',1) for tool in serverTools.split(';')]
	needed_tools = []
	hashed_tools = {}
	for tool in tools.tools :
		hashed_tools[(tool.getToolID(None),hash(open('tools/' +
			tool.__file__).read()))] = tool
	global active_tools
	for tool in all_tools:
		in_cache = hashed_tools.get(tool, None)
		if not in_cache :
			needed_tools.add(tool[0]) #this is the toolID
		else :
			active_tools.append(in_cache)
	return needed_tools

def serialize_tool(toolID):
	s_tool = None
	for tool in tools.tools :
		if tool().getToolID() == toolID :
			s_tool = tool
			break
	if s_tool == None :
		print "no tool: %s" % toolID
		return ""
	if s_tool.__file__ != None :
		return ';'.join((s_tool.__file__,open('tools/%s' %
		s_tool.__file__,'r').read()))
	print "bad"
	return ''

def unserialize_tool(tool_string, client_state = None):
	print "deserializing"
	print tool_string
	if not tool_string :
		return None
	stuff = tool_string.split(';',1)
	file_name = stuff[0]
	contents = stuff[1]
	i = 0
	base_name = os.path.splitext(file_name)[0]
	print base_name
	while os.path.exists('tools/%s'%file_name) :
		file_name = base_name + str(i) + '.py'
		i += 1
	f = open('tools/%s' % file_name, 'w')
	f.write(contents)
	tool = __import__('tools.' + file_name[:-3], fromlist=['*'])
	print tool.__file__
	for name in dir(tool) :
		print name
		obj = getattr(tool,name)
		if isinstance(obj,type) and issubclass(obj, Tool) and not obj == Tool :
			obj.__file__ = file_name
			return obj(client_state)
	return None
if __name__ == "__main__" :
	x = serialize_tool('rect')
	y = unserialize_tool( x)
	print dir(y)
	
