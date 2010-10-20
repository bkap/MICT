from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool
import re
from java.awt.image import BufferedImage
point_re = re.compile(r"\( *(\d+), *(\d+) *\) *")

class PencilTool(Tool) :
	def __init__(self, clientState=None) :
		self.client_state = clientState
		self.prev_point_draw = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(209,149,12))
		g.drawLine(20,0,0,25)
		g.drawLine(25,0,5,25)
		g.drawLine(25,0,27,5)
		g.drawLine(20,0,25,0)
		g.drawLine(27,5,8,27)
		g.setColor(Color(0,0,0))
		g.drawLine(0,25,5,32)
		g.drawLine(5,25,5,32)
		g.drawLine(5,25,0,25)
		g.drawLine(8,27,0,25)
		g.drawLine(8,27,5,32)
	def __repr__(self) :
		return self.getToolName()
	def mousePressed(self, locationOnScreen, g) :
		self.prev_point = locationOnScreen
		self.points = [(locationOnScreen.x, locationOnScreen.y)]
		return ""
	def mouseDragged(self, locationOnScreen, g) :
		x0, y0 = self.prev_point.x, self.prev_point.y
		x1,y1 = locationOnScreen.x, locationOnScreen.y
		self.prev_point = locationOnScreen
		return self._getmetadata() + "|" + "(%d,%d);(%d,%d) " % (x0,y0,x1,y1)
	def _getmetadata(self) :
		return "%s" % self.client_state.selectedColor.getRGB()
	def mouseReleased(self, locationOnScreen, g) :
		self.mouseDragged(locationOnScreen, g)
		self.prev_point = None
		return ""
	def draw(self, s, g) :
		#TODO: need to redo this method to use the new scheme
		if  s == "":
			return
		try :
			metadata, points = s.split('|')
		except ValueError :
			#no metadata given. This is a problem
			return
		points = points.split(';')
		
		#process metadata
		color = int(metadata)

		prev_point = None
		
		if len(points) > 1 :
			#this better be true.
			g.setColor(Color(color)) 
			for point in points :
				point_match = point_re.match(point)
				if not point_match :
					#this is an error, shouldn't happen. Figure out what to do
					#we were sent bad data
					print "no match"
					return
				x,y = point_match.groups()
				x,y = int(x), int(y)
				if prev_point :
					g.drawLine(prev_point[0], prev_point[1], x, y)
				prev_point = (x,y)
	def getIcon(self) :
		return self.image
	def getToolName(self) :
		return "Pencil"
	def getTooltip(self) :
		return "Draw wherever the mouse goes"
	def getToolID(self) :
		return "pencil"
	def getAffectedArea(self, phrase) :
		points = phrase.split('|')[1].split(';')
		match = point_re.match(points[0])
		if match is None:
			print "match is none. should not happen. ever."
			return
		x1, y1 = match.groups()
		x1, y1 = int(x1), int(y1)
		match = point_re.match(points[1])
		if match is None:
			print "match is none. should not happen. ever."
			return
		x2, y2 = match.groups()
		x2, y2 = int(x2), int(y2)
		ax1 = min(x1, x2)
		ay1 = min(y1, y2)
		ax2 = max(x1, x2)
		ay2 = max(y1, y2)
		return [ax1, ay1, ax2 - ax1, ay2 - ay1]

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
		g.setColor(self.client_state.selectedColor)
		g.fillRect(x1, y1, x2 - x1, y2 - y1)
		return ""
	def mouseReleased(self, locationOnScreen, g) :
		x1 = min(self.start_point.x, locationOnScreen.x)
		y1 = min(self.start_point.y, locationOnScreen.y)
		x2 = max(self.start_point.x, locationOnScreen.x)
		y2 = max(self.start_point.y, locationOnScreen.y)
		return self._getmetadata() + "|" + "(%d,%d);(%d,%d) " % (x1, y1, x2 - x1, y2 - y1)
	def _getmetadata(self) :
		return "%d" % self.client_state.selectedColor.getRGB()
	
	def draw(self, s, g) :
		if s == "" :
			return
		metadata, points = s.split('|')
		points = points.split(';')
		g.setColor(Color(int(metadata)))
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
		g.fillRect(x1, y1, x2, y2)
	def getAffectedArea(self, phrase) :
		points = phrase.split('|')[1].split(';')
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
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.drawLine(0,0,32,32)
	def mousePressed(self, locationOnScreen, g) :
		self.start_point = locationOnScreen
		return ""
	def mouseHovered(self, locationOnScreen, g) :
		return ""
	def mouseDragged(self, locationOnScreen, g) :
		x1 = self.start_point.x
		y1 = self.start_point.y
		x2 = locationOnScreen.x
		y2 = locationOnScreen.y
		g.setColor(self.client_state.selectedColor)
		g.drawLine(x1, y1, x2, y2)
		return ""
	def _getmetadata(self) :
		''' gets the color and the thickness (currently hardcoded to 1 since
		that isn't implemented yet'''
		
		return "%d;%d" % (self.client_state.selectedColor.getRGB(), 1)
	def mouseReleased(self, locationOnScreen, g) :
		x1 = self.start_point.x
		y1 = self.start_point.y
		x2 = locationOnScreen.x
		y2 = locationOnScreen.y
		return self._getmetadata() + "|" + "(%d,%d);(%d,%d) " % (x1, y1, x2, y2)
	def draw(self, s, g) :
		if s == "" :
			return
		metadata, points = s.split('|')
		color, size = metadata.split(';')
		color, size = int(color), int(size)
		points = points.split(';')
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
		g.setColor(Color(color))
		g.drawLine(x1, y1, x2, y2)
	def getAffectedArea(self, phrase) :
		points = phrase.split('|')[1].split(';')
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
		return self.image
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
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.drawOval(0,0,30,30)
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
		points = phrase.split('|')[1].split(';')
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
		return self.image
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
