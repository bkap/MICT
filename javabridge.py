from javax.swing import JLabel, JPanel, JButton
def get_tools(*args, **kwargs) :
    server_url = args[0]
    graphics = args[1]
    label = JLabel(server_url)
    panel = JPanel()
    panel.add(label)
    panel.add(JButton())
    return panel


dispatcher = {"getTools":get_tools}
def bridge(caller, *args, **kwargs) :
   return dispatcher[caller](*args, **kwargs)

from mict.bridge import ClientConnection
class ClientConn(ClientConnection):
    def __init__(self, server, graphics):
        self.server = server
        self.graphics = graphics
        self.maketools()
    def maketools(self) :
        self.tools = []
    def getTools(self):
        return self.tools
    def sendToServer(self, s) :
        pass
        