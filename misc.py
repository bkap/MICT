#this is for random stuff that I need. Like my StringIO wrapper

from java.io import InputStream, OutputStream, ObjectInputStream, ObjectOutputStream
from org.python.util import PythonObjectInputStream
from cStringIO import StringIO
from array import array
class MyObjectInputStream(PythonObjectInputStream) :
	def __init__(self, istr) :
		super(MyObjectInputStream,self).__init__(istr)
	def resolveClass(self, v) :
		clsName = v.getName();
		print "Class: " + clsName
		print "serialVersionUID %d" % v.getSerialVersionUID()
		return self.super__resolveClass(v) 
def getDualStreams() :
	sio = StringIO()
	class ReadStream(InputStream) :
		def __init__(self) :
			self.sio = sio
			self.pos = 0
		def read(self, b=None, off = 0, len_ = -1) :
			sio.seek(self.pos)
			if b :
				if len_ == -1 :
					len_ = len(b)
				if len_ == 0 :
					return 0
				i = 0
				for i in range(len_) :
					if i+off >= len(b) :
						self.pos = sio.tell()
						return i
					char = sio.read(1)
					if char:
						x = ord(char) if ord(char) < 128 else  ord(char) - 256
						b[i+off] = x
					else :
						self.pos = sio.tell()
						return -1
				
				self.pos = sio.tell()
				return i + 1
			x = sio.read(1)
			self.pos = sio.tell()
			if not x :
				return -1
			return ord(x) if ord(x) < 128 else ord(x) - 256
		def readAll(self) :
			sio.seek(self.pos)
			x = sio.read()
			self.pos = sio.tell()
			return x
	class WriteStream(OutputStream) :
		def __init__(self) :
			self.sio = sio
		def writestr(self, s) :
			sio.write(s)
		def write(self, b,off=0,len_=-1) :
			sio.seek(0,2)
			if(isinstance(b,array)) :
				if len_ == -1:
					len_ = len(b)
				for i in range(len_) :
					if off+i>= len(b) :
						break
					sio.write(chr(b[off+i] if b[off+i] >= 0 else 256 + b[off+i]))
			else :
				sio.write(b)
	return ReadStream(), WriteStream()

