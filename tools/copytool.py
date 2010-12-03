from java.awt import Point, Color, Graphics, Image, Stroke, BasicStroke
from java.lang import Math
from mict.tools import Tool
from mict.client import ClientState
import re
from java.awt.image import BufferedImage


class CopyTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		if self.client_state is not None :
			self.canvas_image = self.client_state.canvas.getCanvasImage()
		self.start_point = None
		self.makeImage()	
	

	def makeImage(self) :
		#Draw something to indicate copy tool.
		#Currently drawing an X
		self.image = BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)
		g = self.image.getGraphics()
		g.setColor(Color(0,0,0))
		g.drawLine(7,6,10,3)
		g.drawLine(10,3,22,3)
		g.drawLine(22,3,25,6)
		g.drawLine(7,6,7,26)
		g.drawLine(7,26,10,30)
		g.drawLine(10,30,22,30)
		g.drawLine(22,30,25,27)
		#clear g?


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
		#g.setColor(self.client_state.selectedColor)
		#g.scale(10, 10)
		#g.setStroke(BasicStroke(1.5))
		g.drawRect(x1, y1, x2 - x1, y2 - y1)	#TODO: Ensure this draws to the artifact layer...
		return ""


	def _getmetadata(self) :
		return ""


	def mouseReleased(self, locationOnScreen, g) :
		x1 = self.start_point.x
		y1 = self.start_point.y
		x2 = locationOnScreen.x
		y2 = locationOnScreen.y

		#Save the Image to clipboard.
		dx1 = min(x1, x2)
		dy1 = min(y1, y2)
		dx2 = max(x1, x2)
		dy2 = max(y1, y2)
		img = self.client_state.canvas.getCanvasImage()
		xoff = (img.getWidth(self.client_state.canvas) - self.client_state.canvas.getWidth()) / 2
		yoff = (img.getHeight(self.client_state.canvas) - self.client_state.canvas.getHeight()) / 2
		self.client_state.clipboard = img.getSubimage(dx1+xoff, dy1+yoff, dx2-dx1, dy2-dy1)
		
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


	def getAffectedArea(self, phrase) :		#TODO: Determine if this method is necessary
		return None


	def getIcon(self) :
		return self.image


	def getToolName(self) :
		return "Copy"


	def getTooltip(self) :
		return "Copy the selected area into a clipboard for future use."


	def getToolID(self) :
		return 'copy'
