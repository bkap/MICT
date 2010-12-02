from java.awt.image import BufferedImage
from java.awt import Point, Color, Graphics, Image
from mict.tools import Tool, ImageData
from javax.swing import JFileChooser
from javax.swing.filechooser import FileNameExtensionFilter
from javax.imageio import ImageIO
from java.io import File
import os.path
import base64
class ImageTool(Tool) :
	def __init__(self, clientstate=None) :
		self.client_state = clientstate
		self.sendImage = None
		self.getimage()
	def getimage(self) :
		if not os.path.exists('tools/image.png') :
			imagedata = 'iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJ\nbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdp\nbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6\neD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEz\nNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJo\ndHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlw\ndGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAv\nIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RS\nZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpD\ncmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBNYWNpbnRvc2giIHhtcE1NOkluc3RhbmNl\nSUQ9InhtcC5paWQ6MzZDNTFCOUZGNUQ5MTFERkE0Mzk4NUE5OTMzRTQwNzkiIHhtcE1NOkRvY3Vt\nZW50SUQ9InhtcC5kaWQ6MzZDNTFCQTBGNUQ5MTFERkE0Mzk4NUE5OTMzRTQwNzkiPiA8eG1wTU06\nRGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDozNkM1MUI5REY1RDkxMURGQTQz\nOTg1QTk5MzNFNDA3OSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDozNkM1MUI5RUY1RDkxMURG\nQTQzOTg1QTk5MzNFNDA3OSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1w\nbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pms/CZAAAAECSURBVHjaYtRe+JiBloCJgcZg1IIhZYHV\ntzNARKoFLESqY/3/p+DNHCDjtKzBb0YW6vvA9/Numd/Pgcj/007qBxHfvy9p75ZC2CnvlwO5VLYg\n7d0Svr+foZb9/QzkUtMC1Z/3A1CDBcgFClLNgqK3s5j+/0PR8/8fUJA6Fth+PaX//RqmOFAQKEWp\nBcCkmft2Hi5ZoBRQAUUWRHzcCEyXuGSBUkAF5Fsg+udtwvtV+DUDFQCVkWlB9ruFnP9+4NcMVABU\nRo4FwFTo9vkgMXEIVIY/yWIvVTR+3dnI505kQgQqvs2uSJoF/nltxOdVVQaGzfNdRyucUQtGLRi1\ngAIAEGAAimxsT0J9RpkAAAAASUVORK5CYII=\n'
			actual_image = base64.b64decode(imagedata) 
			from java.io import ByteArrayInputStream
			stream = ByteArrayInputStream(actual_image)
			self._image = ImageIO.read(stream);
		else :
			self._image = ImageIO.read(File('tools/image.png'))
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
		return self._image #TODO: implement
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
	def getLastImage(self) :
		if self.sendImage :
			image = self.sendImage
			self.sendImage = None
			return image
		return None
