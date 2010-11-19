from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool
import re
from java.awt.image import BufferedImage
point_re = re.compile(r"\( *(\d+), *(\d+) *\) *")

class RectangleTool(Tool) :
	ICON_SIZE = 32
	ZERO = 0
	THIRTY_ONE = 31
	serialVersionUID = 123456789
	def __init__(self, clientState = None) :
		print type(clientState)
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(self.ICON_SIZE,self.ICON_SIZE,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(self.ZERO,self.ZERO,self.ZERO))
		g.drawRect(self.ZERO,self.ZERO,self.THIRTY_ONE,self.THIRTY_ONE)
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
