from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool
import re
from java.awt.image import BufferedImage
point_re = re.compile(r"\( *(\d+), *(\d+) *\) *")

#class PanTool(Tool) :
#	def __init__(self, clientState = None) :
#		self.client_state = clientState
#		self.start_point = None
#		self.makeImage()
#	def makeImage(self) :
#		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
#		g = self.image.getGraphics()
#		g.setColor(Color(0,0,0))
#		g.drawLine(16,0,16,32)
#		g.drawLine(0,16,32,16)
#		g.drawLine(16,0,10,6)
#		g.drawLine(16,0,22,6)
#		g.drawLine(0,16,6,10)
#		g.drawLine(0,16,6,22)
#		g.drawLine(16,32,10,26)
#		g.drawLine(16,32,22,26)
#		g.drawLine(32,16,26,10)
#		g.drawLine(32,16,26,22)
#	def mousePressed(self, locationOnScreen, g) :
#		self.last_point = locationOnScreen
#		return ""
#	def mouseHovered(self, locationOnScreen, g) :
#		return ""
#	def mouseDragged(self, locationOnScreen, g) :
#		dx = locationOnScreen.x - self.last_point.x
#		dy = locationOnScreen.y - self.last_point.y
#		self.last_point = locationOnScreen
#		return "%d,%d" % (dx, dy)
#	def mouseReleased(self, locationOnScreen, g) :
#		dx = locationOnScreen.x - self.last_point.x
#		dy = locationOnScreen.y - self.last_point.y
#		self.last_point = None
#		return "%d,%d" % (dx, dy)
#	def draw(self, s, g) :
#		return None
#	def getIcon(self) :
#		return self.image
#	def getToolName(self) :
#		return "Pan"
#	def getTooltip(self) :
#		return "move to a new section of the canvas defined by the starting point and the point at which the mouse was released"
#	def getToolID(self) :
#		return 'pan'	

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
		x0,y0 = self.prev_point.x, self.prev_point.y
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
		
		print "have points"
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
				print "drawing line"
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

class RectangleTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.drawRect(0,0,31,31)
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
		g.drawRect(x1, y1, x2 - x1, y2 - y1)
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
		g.drawRect(x1, y1, x2, y2)
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
		return "Rectangle"
	def getTooltip(self) :
		return "draw a rectangle with one corner\nwhere you click and the opposite\ncorner where you release the mouse"
	def getToolID(self) :
		return 'rect'   

class FilledRectangleTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.fillRect(0,0,32,32)
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
		return self.image
	def getToolName(self) :
		return "Filled Rectangle"
	def getTooltip(self) :
		return "draw a filled rectangle with one corner\nwhere you click and the opposite\ncorner where you release the mouse"
	def getToolID(self) :
		return 'fillRect'   

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
		g.setColor(self.client_state.selectedColor)		
		g.drawOval(x1, y1, x2 - x1, y2 - y1)
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

class FilledOvalTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.fillOval(0,0,30,30)
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
		g.fillOval(x1, y1, x2 - x1, y2 - y1)
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
		g.fillOval(x1, y1, x2, y2)
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
		return "FilledOval"
	def getTooltip(self) :
		return "draw a Filled Oval inscribed in the imaginary rectagle with one corner\nwhere you click and another\ncorner where you release the mouse"
	def getToolID(self) :
		return 'filledoval'	

class RoundedRectangleTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.drawRoundRect(0,0,31,31,10,10)
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
		g.drawRoundRect(x1, y1, x2 - x1, y2 - y1, 20, 20)
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
		g.drawRoundRect(x1, y1, x2, y2, 20, 20)
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
		return "Rounded Rectangle"
	def getTooltip(self) :
		return "draw a rounded rectangle with one corner\nwhere you click and the opposite\ncorner where you release the mouse"
	def getToolID(self) :
		return 'roundrect'

class FilledRoundedRectangleTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.fillRoundRect(0,0,30,30,10,10)
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
		g.fillRoundRect(x1, y1, x2 - x1, y2 - y1,20,20)
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
		g.fillRoundRect(x1, y1, x2, y2,20,20)
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
		return [ax1, ay1, ax2 - ax1 + 1, ay2 - ay1 + 1]
	def getIcon(self) :
		return self.image
	def getToolName(self) :
		return "Filled Rounded Rectangle"
	def getTooltip(self) :
		return "draw a filled rounded rectangle with one corner\nwhere you click and the opposite\ncorner where you release the mouse"
	def getToolID(self) :
		return 'fillRoundRect'

def _get_tools() :
	#hacky way to get the list of tools
	tools = []
	for key, item in globals().iteritems() :
		if isinstance(item, type) and issubclass(item, Tool) and item.__name__ != "Tool":
			tools.append(item)
	print tools
	return tools
tools = _get_tools()
