from java.awt import Point, Graphics, Image
from mict.tools import Tool
import re

point_re = re.compile(r"\((\d+),(\d+)\)")
class PencilTool(Tool) :
    def __init__(self, clientState=None) :
        super(PencilTool,self).__init__()
        self.client_state = clientState
        self.prev_point_draw = None
    def __repr__(self) :
        return self.getToolName()
    def mousePressed(self, locationOnScreen, g) :
        self.prev_point = locationOnScreen
        self.points = [locationOnScreen]
        return "(%d,%d)" % (locationOnScreen.x, locationOnScreen.y)
    def mouseMoved(self, locationOnScreen, g) :
        g.drawLine(self.prev_point.x, self.prev_point.y, locationOnScreen.x,
        locationOnScreen.y)
        self.prev_point = locationOnScreen
        self.points.append(locationOnScreen)
        return "(%d, %d)" % (locationOnScreen.x, locationOnScreen.y)
    def mouseReleased(self, locationOnScreen, g) :
       return "()"
    def serialize(self) :
        return ';'.join(self.points)
    def draw(self, s, g) :
        points = s.split(';')
        prev_point = None
        if len(points) > 1 :
            #it's a full draw
            for point in points :
                point_match = point_re.match(point)
                if not point_match :
                    #this is an error, shouldn't happen. Figure out what to do
                    #we were sent bad data
                    return False
                x,y = point_match.groups()
                if prev_point :
                    g.drawLine(prev_point[0], prev_point[1], x, y)
                prev_point = (x,y)
            return True
        #at this point, we know it's just a single point that's been given
        if(s == "()") :
            #this signifies a mouse released event
            #the draw is over
            self.prev_point_draw = None
            return
        #we're in the middle of a draw
        match = point_re.match(s)
        x, y = match.groups()
        if self.prev_point_draw :
            g.drawLine(self.prev_point_draw[0], self.prev_point_draw[1],x,y)
        self.prev_point_draw = (x,y)
    def getImage(self) :
        return None
    def getToolName(self) :
        return "Pencil"
    def getTooltip(self) :
        return "Draw wherever the mouse goes"
    def getToolID(self) :
        return 1
class RectangleTool(Tool) :
    def __init__(self, clientState = None) :
        super(RectangleTool, self).__init__()
        self.client_state = clientState
        self.start_point = None
        self.end_point = None
    def mousePressed(self, locationOnScreen, g) :
        self.start_point = locationOnScreen
        self.end_point = None
        return "(%d, %d)" % (locationOnScreen.x, locationOnScreen.y)
    def mouseDragged(self, locationOnScreen, g) :
        return ''
    def mouseReleased(self, locationOnScreen, g) :
        self.end_point = locationOnScreen
        g.drawRect(self.start_point.x, self.start_point.y, self.end_point.x,
        self.end_point.y)
        return "(%d, %d)" % (self.end_point.x, self.end_point.y)
    def serialize(self) :
        if not self.end_point :
            #this should not happen. We don't have a valid rectangle
            return ""
        return "(%d,%d);(%d,%d)" % (self.start_point.x, self.start_point.y,
            self.end_point.x, self.end_point.y)
    def draw(slef, s, g) :
       #TODO: implement this
    def getIcon(self) :
        pass
    def getName(self) :
        return "rectangle"
    def getTooltip(self) :
        return "draw a rectangle with one corner\nwhere you click and
        another\ncorner where you release the mouse"
    def getToolID(self) :
        return 2
def _get_tools() :
    #hacky way to get the list of tools
    tools = []
    for item in globals() :
        if issubclass(item, Tool) :
            tools.append(item)
    return tools
tools = get_tools()
