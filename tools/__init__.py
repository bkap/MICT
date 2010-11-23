from mict.tools import Tool
import os.path
import hashlib
def get_tool_files_and_hashes() :
	tools = []
	tooldir = os.path.dirname(__file__)
	for f_name in os.listdir(tooldir) :
		if f_name.endswith('.py') and f_name != "__init__.py" :
			tools.append(f_name + ';' + hashlib.sha1(open(os.path.join(tooldir, f_name),'r').read()).hexdigest())
	return ':'.join(tools)
tools = []
def check_files(filestr) :
	global tools
	tooldir = os.path.dirname(__file__)
	needed = []
	for tool in filestr.split(':'):
		tool_file, sha1 = tool.split(';',1)
		tool_f = os.path.join(tooldir, tool_file)
		if not os.path.exists(os.path.join(tooldir,tool_file)) or hashlib.sha1(open(tool_f)).hexdigest() != sha1 :
			needed.append(tool_file)
		else :
			tools.extend(load_tools(tool_file))
	return ':'.join(needed)
def load_tools(filename) :
	tools = []
	module = os.path.splitext(filename)[0]
	toolmodule = __import__("tools." + module,fromlist=['__name__'])
	for name in dir(toolmodule) :
			obj = getattr(toolmodule, name)
			if isinstance(obj, type) and issubclass(obj, Tool) and not obj == Tool:
				obj.__file__ = filename
				tools.append(obj)
	return tools

def add_file(filestr) :
	filename, filetxt = filestr.split(':',1)
	tooldir = os.path.dirname(__file__)
	toolfile = os.path.join(tooldir, filename)
	if os.path.exists(toolfile):
		os.remove(toolfile)
	f = open(toolfile,'w')
	f.write(filetxt)
	f.close()
	newtools = (load_tools(filename))
	tools.extend(newtools)
	return newtools
def _get_tools() :
	#hacky way to get the list of tools
	tools = []
	for key, item in globals().iteritems() :
		if isinstance(item, type) and issubclass(item, Tool) and item.__name__ != "Tool":
			tools.append(item)
	print tools
	
	#check other files in this package
	import os
	import os.path
	print "os.path imported"
	for filename in os.listdir('./tools') :
		print filename
		if filename.endswith('.py') and filename != "__init__.py" :
			#import the file
			tools.extend(load_tools(filename))
	return tools
def get_tool_definitions(toolnames) :
	'''returns the text of all tool files
	for the tools with the given names. If the tool is defined in __init__.py,
	it doesn't get included
	toolnames is a collection of toolIDs to get'''
	tool_file_text = []
	for tool in tools :
		if tool().getToolID() in toolnames and hasattr(tool,'__file__') :
			tool_file_text.append(tool().getToolID() +  ';' + open('tools/%s' % tool.__file__,'r').read())
	return tool_file_text
def get_tool_file(f) :
	return f + ':' + open('tools/%s' % f,'r').read()

def reload_tools() :
	global tools
	tools = _get_tools()
