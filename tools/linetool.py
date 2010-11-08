from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool
import re
from java.awt.image import BufferedImage


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


