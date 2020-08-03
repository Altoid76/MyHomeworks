
class Rover:
	def __init__(self,position,energy):
		"""
		Initialises the rover
		"""
		self.position=position
		self.energy=energy
		self.x=self.position[1]
		self.y=self.position[0]
		self.explored=[]
		if (self.y,self.x) not in self.explored:
			self.explored.append((self.y,self.x))
	def move(self,direction,cycles,planet,tiles):
		board=planet.make_board(tiles)
		cycles=int(cycles)
		if self.energy<cycles:
			cycles=self.energy
		if direction=='N':
			for i in range(cycles):
				if board[self.y-1][self.x].is_slope():
					if board[self.y-1][self.x].low==board[self.y][self.x].high:
						board[self.y][self.x].high=board[self.y-1][self.x].high
					elif board[self.y-1][self.x].high==board[self.y][self.x].high:
						board[self.y][self.x].high=board[self.y-1][self.x].low
					else:
						break
				else:
					if board[self.y-1][self.x].high!=board[self.y][self.x].high:
						break
				if self.y<0:
					self.y=planet.height-1
				self.y=self.y-1
				if (self.y,self.x) not in self.explored:
					self.explored.append((self.y,self.x))
				if board[self.y][self.x].is_shaded():
					self.energy=self.energy-1
		elif direction=='S':
			for i in range(cycles):
				if self.y==planet.height-1:
					self.y=-1
				#To judge whether the block in front of the rover on its direction is slope or not 
				if board[self.y+1][self.x].is_slope():
					if board[self.y+1][self.x].low==board[self.y][self.x].high:
						board[self.y][self.x].low=board[self.y+1][self.x].high
					elif board[self.y+1][self.x].high==board[self.y][self.x].high:
						board[self.y][self.x].low=board[self.y+1][self.x].low
					else:
						break
				#if not, it is only a tile.So 
				else:
					if board[self.y+1][self.x].high!=board[self.y][self.x].high:
						break
				self.y=self.y+1
				if (self.y,self.x) not in self.explored:
					self.explored.append((self.y,self.x))
				if board[self.y][self.x].is_shaded():
					self.energy=self.energy-1
		elif direction=='W':
			for i in range(cycles):
				if board[self.y][self.x-1].is_slope():
					if board[self.y][self.x-1].low==board[self.y][self.x].high:
						board[self.y][self.x].high=board[self.y][self.x-1].high
					elif board[self.y][self.x-1].high==board[self.y][self.x].high:
						board[self.y][self.x].high=board[self.y][self.x-1].low
					else:
						break
				else:
					if board[self.y][self.x-1].high!=board[self.y][self.x].high:
						break
				if self.x<0:
					self.x=planet.width-1
				self.x=self.x-1
				if (self.y,self.x) not in self.explored:
					self.explored.append((self.y,self.x))
				if board[self.y][self.x].is_shaded():
					self.energy=self.energy-1
		elif direction=='E':
			for i in range(cycles):
				if self.x==planet.width-1:
					self.x=-1
				if board[self.y][self.x+1].is_slope():
					if board[self.y][self.x+1].low==board[self.y][self.x].high:
						board[self.y][self.x].low=board[self.y][self.x+1].high
					elif board[self.y][self.x+1].high==board[self.y][self.x].high:
						board[self.y][self.x].low=board[self.y][self.x+1].low
					else:
						break
				else:
					if board[self.y][self.x+1].high!=board[self.y][self.x].high:
						break
				self.x=self.x+1
				if (self.y,self.x) not in self.explored:
					self.explored.append((self.y,self.x))
				if board[self.y][self.x].is_shaded():
					self.energy=self.energy-1
	def wait(self,cycles,planet,tiles):
		"""
		The rover will wait for the specified cycles
		"""
		board=planet.make_board(tiles)
		cycles=int(cycles)
		for i in range(cycles):
			if board[self.y][self.x].is_shaded()!=True:
				self.energy=self.energy+1
	def Scan(self,mode,planet,tiles):
		board=planet.make_board(tiles)
		if mode=='shade':
			graph='|'
			scanned=[]
			for j in range(self.y-2,self.y+3):
				for i in range(self.x-2,self.x+3):
					j=j%planet.height
					i=i%planet.width
					if j!=self.y or i!=self.x:
						if board[j][i].is_shaded()==True:
							graph=graph+'#|'
						else:
							graph=graph+' |'
					else:
						graph=graph+'H|'
					if (j,i) not in self.explored:
						self.explored.append((j,i))
				scanned.append(graph)
				graph='|'
			return scanned
		elif mode=='elevation':
			a='|'
			scanned=[]
			for j in range(self.y-2,self.y+3):
				for i in range(self.x-2,self.x+3):
					j=j%planet.height
					i=i%planet.width
					if board[j][i].is_slope():
						if j!=self.y or i!=self.x:
							if board[j][i].low==board[self.y][self.x].high:
								a=a+'\\|'
							elif board[j][i].high==board[self.y][self.x].high:
								a=a+'/|'
							elif board[j][i].high<board[self.y][self.x].high:
								a=a='-|'
							elif board[j][i].low>board[self.y][self.x].high:
								a=a+'+|'
						else:
							a=a+'H|'
					else:
						if j!=self.y or i!=self.x:
							if board[j][i].high>board[self.y][self.x].high:
								a=a+'+|'
							elif board[j][i].high<board[self.y][self.x].high:
								a=a+'-|'
							else:
								a=a+' |'
						else:
							a=a+'H|'
					if (j,i) not in self.explored:
						self.explored.append((j,i))
				scanned.append(a)
				a='|'
			return scanned
	def calculate_explored(self,planet,tiles):
		area=len(self.explored)
		total=planet.width*planet.height
		rate=int((area/total)*100)
		if rate>100:
			rate=100
		return rate
				
