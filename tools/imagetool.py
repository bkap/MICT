from java.awt.image import BufferedImage
from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool, ImageData
from javax.swing import JFileChooser
from javax.swing.filechooser import FileNameExtensionFilter
from javax.imageio import ImageIO

class ImageTool(Tool) :
	def __init__(self, clientstate=None) :
		self.client_state = clientstate
		self.sendImage = None
	def get_image_file(self) :
		file_dialog = JFileChooser()
		#image files
		image_filter = FileNameExtensionFilter("Image Files", 
			["jpg","bmp","png","gif"])
		print image_filter.getExtensions()
		file_dialog.setFileFilter(image_filter)
		x = file_dialog.showOpenDialog(None)
		if x == JFileChooser.APPROVE_OPTION :
			return file_dialog.getSelectedFile()
		else :
			return None
	def getToolID(self) :
			return "image"
	def getIcon(self) :
		return None #TODO: implement
	def getToolName(self) :
		return "Image"
	def getTooltip(self) :
		return "click to insert an image"
	def mouseClicked(self,s,g) :
		f = self.get_image_file()
		#TODO: figure out how to extract the image
		#and encode into a string
		if f:
			image = ImageIO.read(f)
			self.sendImage = ImageData(s.x, s.y, image)
		return ''
	def draw(self, s,g) :
		pass
	def getLastImage() :
		if self.sendImage :
			image = self.sendImage
			self.sendImage = None
			return image
		return None
