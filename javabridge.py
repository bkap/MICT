from javax.swing import JLabel, JPanel, JButton
from mict.bridge import ClientConnection

import tools
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


    
class ClientConn(ClientConnection):
    def __init__(self, server, graphics, clientstate):
        super(ClientConn, self).__init__()
        self.server = server
        self.graphics = graphics
        self.maketools(clientstate)
    def maketools(self, clientstate) :
        self._tools = []
        for tool in tools.tools :
            self._tools.append(tool(clientstate))
        print self._tools
    def sendToServer(self, s) :
        pass
    def receiveFromServer(self) :
        return ""
        