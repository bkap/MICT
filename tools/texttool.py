from java.awt import Point, Color, Graphics, Font
from java.awt.image import BufferedImage
from java.awt.font import TextLayout, FontRenderContext
from mict.tools import Tool, ImageData
from javax.swing import JOptionPane
from mict.networking import EscapingInputStream, EscapingOutputStream
from java.io import ByteArrayOutputStream
import re
point_re = re.compile(r"\( *(\d+), *(\d+) *\) *")

class TextTool(Tool) :
	def __init__(self, clientstate=None) :
		self.client_state = clientstate
		self.font = Font.decode(None)
		self.context = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics().getFontRenderContext()
	def getToolID(self) :
		return "text"
	def getIcon(self) :
		return None #TODO: implement
	def getToolName(self) :
		return "Text"
	def getTooltip(self) :
		return "click to insert text"
	def mouseClicked(self, s, g) :
		x = JOptionPane.showInputDialog("Type your text here:", self.client_state)
		if x is None:
			x = ''
		bout = ByteArrayOutputStream()
		eout = EscapingOutputStream(bout)
		eout.write(x)
		y = bout.toString()
		return self._getmetadata() + "|" + "(%d,%d)|%s " % (s.x, s.y, y)
	def _getmetadata(self) :
		return "%d" % self.client_state.selectedColor.getRGB()
	def draw(self, s, g) :
		if s == "" :
			return
		metadata, point, string = s.split('|')
		string = EscapingInputStream.read(string)
		g.setColor(Color(int(metadata)))
		match = point_re.match(point)
		if match is None:
			return
		x, y = match.groups()
		g.drawString(string, x, y)
	def getAffectedArea(self, phrase) :
		if s == "" :
			return
		metadata, point, string = s.split('|')
		g.setColor(Color(int(metadata)))
		match = point_re.match(point)
		if match is None:
			return
		x, y = match.groups()
		rect = TextLayout(string, font, context).getBounds()
		return [rect.getX() + x, rect.getY() + y, rect.getWidth(), rect.getHeight()]
