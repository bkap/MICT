from java.awt.image import BufferedImage
from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool
from javax.swing import JFileChooser
from javax.swing.filechooser import FileNameExtensionFilter
class ImageTool(Tool) :
	def __init__(self, clientstate=None) :
		self.client_state = clientstate
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
		return ''
	def draw(self, s,g) :
		pass
