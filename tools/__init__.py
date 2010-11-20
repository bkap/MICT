from mict.tools import Tool








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
			module = os.path.splitext(filename)[0]
			print module
			toolmodule = __import__("tools." + module,fromlist=['__name__'])
			print dir(toolmodule)
			for name in dir(toolmodule) :
				print name
				obj = getattr(toolmodule, name)
				if isinstance(obj, type) and issubclass(obj, Tool) and not obj == Tool:
					obj.__file__ = filename
					tools.append(obj)
	return tools
tools = _get_tools()
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

def reload_tools() :
	global tools
	tools = _get_tools()
