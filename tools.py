from java.awt import Point, Graphics, Image
from mict.tools import Tool
class PencilTool(Tool) :
    def __init__(self, clientState=None) :
        super(PencilTool,self).__init__()
        self.client_state = clientState
    def __repr__(self) :
        return self.getToolName()
    def mousePressed(locationOnScreen, locationOnCanvas, g) :
        self.prev_point = locationOnScreen
        self.points = locationOnCanvas
    
    def mouseMoved(locationOnScreen, locationOnCanvas, g) :
        g.drawLine(self.prev_point.x, self.prev_point.y, locationOnScreen.x,
        locationOnScreen.y)
        self.prev_point = locationOnScreen
        self.points.append(locationOnCanvas)
        
    def mouseReleased(locationOnScreen, locationOnCanvas, g) :
       pass
    def serialize(self) :
        return ','.join(self.points)
    def drawFromString(s, g) :
        pass
    def getImage(self) :
        return None
    def getToolName(self) :
        return "Pencil"
    def getTooltip(self) :
        return "Draw wherever you draw your mouse"

tools = [PencilTool]
