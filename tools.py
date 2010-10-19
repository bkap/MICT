from java.awt import Point, Graphics, Image
from mict.tools import Tool
import re

point_re = re.compile(r"\( *(\d+), *(\d+) *\) *")

class PencilTool(Tool) :
	def __init__(self, clientState=None) :
		self.client_state = clientState
	def __repr__(self) :
		return self.getToolName()
	def mousePressed(self, locationOnScreen, g) :
		self.prev_point = locationOnScreen
		return ""
	def mouseHovered(self, locationOnScreen, g) :
		return ""
	def mouseDragged(self, locationOnScreen, g) :
		phrase = "(%d,%d);(%d,%d) " % (locationOnScreen.x, locationOnScreen.y, self.prev_point.x, self.prev_point.y)
		self.prev_point = locationOnScreen
		return phrase
	def mouseReleased(self, locationOnScreen, g) :
		g.drawLine(locationOnScreen.x, locationOnScreen.y, self.prev_point.x, self.prev_point.y)
		phrase = "(%d,%d);(%d,%d) " % (locationOnScreen.x, locationOnScreen.y, self.prev_point.x, self.prev_point.y)
		self.prev_point = None
		return phrase
	def draw(self, s, g) :
		if s == "" :
			return
		points = s.split(';')
		print "tools.py: pencil.draw:", s
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		g.drawLine(x1, y1, x2, y2)
	def getAffectedArea(self, phrase) :
		points = phrase.split(';')
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		ax1 = min(x1, x2)
		ay1 = min(y1, y2)
		ax2 = max(x1, x2)
		ay2 = max(y1, y2)
		return [ax1, ay1, ax2 - ax1, ay2 - ay1]
	def getImage(self) :
		return None
	def getToolName(self) :
		return "Pencil"
	def getTooltip(self) :
		return "Draw wherever the mouse goes"
	def getToolID(self) :
		return "pencil"

class RectangleTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
	def mousePressed(self, locationOnScreen, g) :
		self.start_point = locationOnScreen
		return ""
	def mouseHovered(self, locationOnScreen, g) :
		return ""
	def mouseDragged(self, locationOnScreen, g) :
		x1 = min(self.start_point.x, locationOnScreen.x)
		y1 = min(self.start_point.y, locationOnScreen.y)
		x2 = max(self.start_point.x, locationOnScreen.x)
		y2 = max(self.start_point.y, locationOnScreen.y)
		g.drawRect(x1, y1, x2 - x1, y2 - y1)
		return ""
	def mouseReleased(self, locationOnScreen, g) :
		x1 = min(self.start_point.x, locationOnScreen.x)
		y1 = min(self.start_point.y, locationOnScreen.y)
		x2 = max(self.start_point.x, locationOnScreen.x)
		y2 = max(self.start_point.y, locationOnScreen.y)
		return "(%d,%d);(%d,%d) " % (x1, y1, x2 - x1, y2 - y1)
	def draw(self, s, g) :
		if s == "" :
			return
		points = s.split(';')
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		g.drawRect(x1, y1, x2, y2)
	def getAffectedArea(self, phrase) :
		points = phrase.split(';')
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		ax1 = min(x1, x2)
		ay1 = min(y1, y2)
		ax2 = max(x1, x2)
		ay2 = max(y1, y2)
		return [ax1, ay1, ax2 - ax1, ay2 - ay1]
	def getIcon(self) :
		pass
	def getToolName(self) :
		return "Rectangle"
	def getTooltip(self) :
		return "draw a rectangle with one corner\nwhere you click and the opposite\ncorner where you release the mouse"
	def getToolID(self) :
		return 'rect'   

class LineTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
	def mousePressed(self, locationOnScreen, g) :
		self.start_point = locationOnScreen
		return ""
	def mouseHovered(self, locationOnScreen, g) :
		return ""
	def mouseDragged(self, locationOnScreen, g) :
		x1 = min(self.start_point.x, locationOnScreen.x)
		y1 = min(self.start_point.y, locationOnScreen.y)
		x2 = max(self.start_point.x, locationOnScreen.x)
		y2 = max(self.start_point.y, locationOnScreen.y)
		g.drawLine(x1, y1, x2 - x1, y2 - y1)
		return ""
	def mouseReleased(self, locationOnScreen, g) :
		x1 = min(self.start_point.x, locationOnScreen.x)
		y1 = min(self.start_point.y, locationOnScreen.y)
		x2 = max(self.start_point.x, locationOnScreen.x)
		y2 = max(self.start_point.y, locationOnScreen.y)
		return "(%d,%d);(%d,%d) " % (x1, y1, x2 - x1, y2 - y1)
	def draw(self, s, g) :
		if s == "" :
			return
		points = s.split(';')
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		g.drawLine(x1, y1, x2, y2)
	def getAffectedArea(self, phrase) :
		points = phrase.split(';')
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		ax1 = min(x1, x2)
		ay1 = min(y1, y2)
		ax2 = max(x1, x2)
		ay2 = max(y1, y2)
		return [ax1, ay1, ax2 - ax1, ay2 - ay1]
	def getIcon(self) :
		pass
	def getToolName(self) :
		return "Line"
	def getTooltip(self) :
		return "draw a line from the point where you click to the point where\n you release the mouse"
	def getToolID(self) :
		return 'line'	

class OvalTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
	def mousePressed(self, locationOnScreen, g) :
		self.start_point = locationOnScreen
		return ""
	def mouseHovered(self, locationOnScreen, g) :
		return ""
	def mouseDragged(self, locationOnScreen, g) :
		x1 = min(self.start_point.x, locationOnScreen.x)
		y1 = min(self.start_point.y, locationOnScreen.y)
		x2 = max(self.start_point.x, locationOnScreen.x)
		y2 = max(self.start_point.y, locationOnScreen.y)
		g.drawOval(x1, y1, x2 - x1, y2 - y1)
		return ""
	def mouseReleased(self, locationOnScreen, g) :
		x1 = min(self.start_point.x, locationOnScreen.x)
		y1 = min(self.start_point.y, locationOnScreen.y)
		x2 = max(self.start_point.x, locationOnScreen.x)
		y2 = max(self.start_point.y, locationOnScreen.y)
		return "(%d,%d);(%d,%d) " % (x1, y1, x2 - x1, y2 - y1)
	def draw(self, s, g) :
		if s == "" :
			return
		points = s.split(';')
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		g.drawOval(x1, y1, x2, y2)
	def getAffectedArea(self, phrase) :
		points = phrase.split(';')
		match = point_re.match(points[0])
		if match is None:
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		ax1 = min(x1, x2)
		ay1 = min(y1, y2)
		ax2 = max(x1, x2)
		ay2 = max(y1, y2)
		return [ax1, ay1, ax2 - ax1, ay2 - ay1]
	def getIcon(self) :
		pass
	def getToolName(self) :
		return "Oval"
	def getTooltip(self) :
		return "draw an oval inscribed in the imaginary rectagle with one corner\nwhere you click and another\ncorner where you release the mouse"
	def getToolID(self) :
		return 'oval'	

def _get_tools() :
	#hacky way to get the list of tools
	tools = []
	for key, item in globals().iteritems() :
		if isinstance(item, type) and issubclass(item, Tool) and item.__name__ != "Tool":
			tools.append(item)
	print tools
	return tools
tools = _get_tools()
