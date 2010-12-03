from java.awt import Point, Color, Graphics, Image, Stroke, BasicStroke
from java.lang import Math
from mict.tools import Tool, ImageData
from mict.client import ClientState
import re
from java.awt.image import BufferedImage

class PasteTool(Tool) :
	def __init__(self, clientState = None) :
		self.client_state = clientState
		self.start_point = None
		self._image = None
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
		return ""


	def mouseHovered(self, locationOnScreen, g) :
		return ""


	def mouseDragged(self, locationOnScreen, g) :
		return ""


	def mouseReleased(self, locationOnScreen, g) :
		return ""

	def mouseClicked(self, locationOnScreen, g) :
		if self.client_state.clipboard :
			self._image = ImageData(locationOnScreen.x, locationOnScreen.y, self.client_state.clipboard)
		return ""

	def getLastImage(self) :
		if self._image :
			image = self._image
			self._image = None
			return image
		return None

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
