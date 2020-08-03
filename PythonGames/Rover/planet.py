
class Planet:
	def __init__(self, name, width, height):
		"""
		Initialise the planet object
		"""
		pass
		self.name=name
		self.width=width
		self.height=height
	def make_board(self,tiles):
		board=[[0 for i in range(self.width)]for j in range(self.height)]
		for i in range(self.height):
			board[i]=tiles[i*self.width:i*self.width+self.width]
		return board
		
