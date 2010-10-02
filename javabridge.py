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


