#this is for random stuff that I need. Like my StringIO wrapper

from java.io import InputStream, OutputStream
from cStringIO import StringIO
from array import array
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
				x = 0
				for i in range(len_) :
					x = 1
					if i+off >= len(b) :
						break
					char = sio.read(1)
					if char:
						x = ord(char) if ord(char) < 128 else  ord(char) - 256
						print x
						b[i+off] = x
					else :
						self.pos = sio.tell()
						return -1
				if x:
					self.pos = sio.tell()
					return i + 1
				return 0
			x = sio.read(1)
			self.pos = sio.tell()
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
					print b[off+i]
					sio.write(chr(b[off+i] if b[off+i] >= 0 else 256 + b[off+i]))
			else :
				sio.write(b)
	return ReadStream(), WriteStream()

