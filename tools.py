from java.awt import Point, Graphics, Image
from mict.tools import Tool
import re

point_re = re.compile(r"\( *(\d+), *(\d+) *\)")
class PencilTool(Tool) :
    def __init__(self, clientState=None) :
        self.client_state = clientState
        self.prev_point_draw = None
    def __repr__(self) :
        return self.getToolName()
    def mousePressed(self, locationOnScreen, g) :
        self.prev_point = locationOnScreen
        self.points = [(locationOnScreen.x, locationOnScreen.y)]
        return "(%d,%d)" % (locationOnScreen.x, locationOnScreen.y)
    def mouseDragged(self, locationOnScreen, g) :
        self.points.append((locationOnScreen.x, locationOnScreen.y))
        xpoints, ypoints = zip(*self.points)
        g.drawPolyline(xpoints, ypoints, len(xpoints))
        return "(%d, %d)" % (locationOnScreen.x, locationOnScreen.y)
    def mouseReleased(self, locationOnScreen, g) :
        xpoints, ypoints = zip(*self.points)
        g.drawPolyline(xpoints, ypoints, len(xpoints))
        return "()"
    def serialize(self) :
        return ';'.join("(%d,%d)" % point for point in self.points)
    def draw(self, s, g) :
        if s == "()" :
            return
        points = s.split(';')
        prev_point = None
        if len(points) > 1 :
            #it's a full draw
            for point in points :
                point_match = point_re.match(point)
                if not point_match :
                    #this is an error, shouldn't happen. Figure out what to do
                    #we were sent bad data
                    return
                x,y = point_match.groups()
                x,y = int(x), int(y)
                if prev_point :
                    g.drawLine(prev_point[0], prev_point[1], x, y)
                prev_point = (x,y)
        #at this point, we know it's just a single point that's been given
        if(s == "()") :
            #this signifies a mouse released event
            #the draw is over
            self.prev_point_draw = None
            return
        #we're in the middle of a draw
        match = point_re.match(s)
        x, y = match.groups()
        x, y = int(x), int(y)
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
        return "pencil"
class RectangleTool(Tool) :
    def __init__(self, clientState = None) :
        self.client_state = clientState
        self.start_point = None
        self.end_point = None
    def mousePressed(self, locationOnScreen, g) :
        self.start_point = locationOnScreen
        self.end_point = None
        return "(%d, %d)" % (locationOnScreen.x, locationOnScreen.y)
    def mouseDragged(self, locationOnScreen, g) :
        x1 = min(self.start_point.x, locationOnScreen.x)
        y1 = min(self.start_point.y, locationOnScreen.y)
        x2 = max(self.start_point.x, locationOnScreen.x)
        y2 = max(self.start_point.y, locationOnScreen.y)
        g.fillRect(x1, y1, (x2 - x1),
        y2 - y1)
        return ''
    def mouseReleased(self, locationOnScreen, g) :
        x1 = min(self.start_point.x, locationOnScreen.x)
        y1 = min(self.start_point.y, locationOnScreen.y)
        x2 = max(self.start_point.x, locationOnScreen.x)
        y2 = max(self.start_point.y, locationOnScreen.y)
        self.start_point = Point(x1,y1)
        self.end_point = Point(x2,y2)
        g.fillRect(self.start_point.x, self.start_point.y, (self.end_point.x - self.start_point.x),
        self.end_point.y - self.start_point.y)
        return "(%d, %d)" % (self.end_point.x, self.end_point.y)
    def serialize(self) :
        if not self.end_point :
            #this should not happen. We don't have a valid rectangle
            return ""
        return "(%d,%d);(%d,%d)" % (self.start_point.x, self.start_point.y,
            self.end_point.x, self.end_point.y)
    def draw(self, s, g) :
        if s == "()"  or s == "":
            return
        points = s.split(';')
        if len(points) > 1 :
           x1, y1 = point_re.match(points[0]).groups()
           x2,y2 = point_re.match(points[1]).groups()
           x1,x2 = tuple(sorted((int(x1),int(x2))))
           y1,y2 = tuple(sorted((int(y1),int(y2))))
           g.fillRect(x1,y1, (x2-x1),(y2-y1))
        else :
            if not self.start_point :
                print s
                self.start_point = [int(x) for x in point_re.match(s).groups()]
                print "got groups"
            else :
                print s
                x1,y1 = point_re.match(s).groups()
                x1,x2 = tuple(sorted((int(x1),self.start_point[0])))
                y1,y2 = tuple(sorted((int(y1), self.start_point[1])))
                self.start_point = None
                g.fillRect(x1,y1,(x2-x1),(y2-y1))
            
    def getIcon(self) :
        pass
    def getToolName(self) :
        return "rectangle"
    def getTooltip(self) :
        return "draw a rectangle with one corner\nwhere you click and \
        another\ncorner where you release the mouse"
    def getToolID(self) :
        return 'rect'
def _get_tools() :
    #hacky way to get the list of tools
    tools = []
    for key, item in globals().iteritems() :
        if isinstance(item, type) and issubclass(item, Tool) and item.__name__ != "Tool":
            tools.append(item)
    print tools
    return tools
tools = _get_tools()
