from java.awt import Point, Color, Graphics, Image, Stroke, BasicStroke
from java.lang import Math
from mict.tools import Tool
from mict.client import ClientState
import re
from java.awt.image import BufferedImage

class PasteTool(Tool) :
	def __init__(self, clientState = ClientState) :
		self.client_state = clientState
		self.start_point = None
		self.makeImage()
	

	def makeImage(self) :
		#Draw something to indicate copy tool.
		#Currently drawing an X
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.drawLine(0,0,32,32)
		g.drawLine(16,0,0,32)
		g.drawLine(16,0,32,16)
		g.drawLine(32,0,0,32)


	def mousePressed(self, locationOnScreen, g) :
		self.start_point = locationOnScreen
		x1 = self.start_point.x
		y1 = self.start_point.y
		#a = dx1
		#b = dy1
		#for i in range(dx1,dx2+1) :
		#	for j in range(dy1, dy2+1) :
		#		self.client_state.clipboard.setRGB(i-a,j-b, self.canvas_image.getRGB(i,j)) 
		#		#image is just a placeholder of the image... i dunno what its called actually
		#		#Out of the loop
				
		self.getGraphics().drawImage(self.client_state.canvas.getCanvasImage(), -dx1, -dy1, self.client_state.canvas)
		return ""


	def mouseHovered(self, locationOnScreen, g) :
		return ""


	def mouseDragged(self, locationOnScreen, g) :
		return ""


	def mouseReleased(self, locationOnScreen, g) :
		return ""


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
		# important! 
		return None


	def getIcon(self) :
		return self.image


	def getToolName(self) :
		return "Paste"


	def getTooltip(self) :
		return "Paste the Image stored in memory to the area of the screen clicked on by the mouse."


	def getToolID(self) :
		return 'paste'
