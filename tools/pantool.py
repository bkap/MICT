
from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool
import re
from java.awt.image import BufferedImage
point_re = re.compile(r"\( *(\d+), *(\d+) *\) *")

class PanTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	def makeImage(self) :
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.drawLine(16,0,16,32)
		g.drawLine(0,16,32,16)
		g.drawLine(16,0,10,6)
		g.drawLine(16,0,22,6)
		g.drawLine(0,16,6,10)
		g.drawLine(0,16,6,22)
		g.drawLine(16,32,10,26)
		g.drawLine(16,32,22,26)
		g.drawLine(32,16,26,10)
		g.drawLine(32,16,26,22)
	def mousePressed(self, locationOnScreen, g) :
		self.last_point = locationOnScreen
		return ""
	def mouseHovered(self, locationOnScreen, g) :
		return ""
	def mouseDragged(self, locationOnScreen, g) :
		dx = -(locationOnScreen.x - self.last_point.x)
		dy = -(locationOnScreen.y - self.last_point.y)
		self.last_point = locationOnScreen
		return "%d,%d" % (dx, dy)
	def mouseReleased(self, locationOnScreen, g) :
		dx = -(locationOnScreen.x - self.last_point.x)
		dy = -(locationOnScreen.y - self.last_point.y)
		self.last_point = None
		return "%d,%d" % (dx, dy)
	def draw(self, s, g) :
		return None
	def getIcon(self) :
		return self.image
	def getToolName(self) :
		return "Pan"
	def getTooltip(self) :
		return "move to a new section of the canvas defined by the starting point and the point at which the mouse was released"
	def getToolID(self) :
		return 'pan'

