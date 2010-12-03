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
		return [ax1, ay1 - 1, ax2 - ax1, ay2 - ay1 + 2]

