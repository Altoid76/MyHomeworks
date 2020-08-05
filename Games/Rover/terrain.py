
class Tile:	
	def __init__(self,shade,elevation):
		"""
		Initialises the terrain tile and attributes
		"""
		self.elevation=elevation
		if type(self.elevation)==list:
			self.high=int(elevation[1])
			self.low=int(elevation[0])
		else:
			self.high=int(elevation)
		self.shade=shade
	def is_shaded(self):
		"""
		Returns True if the terrain tile is shaded, otherwise False
		"""
		shaded=False
		if self.shade=='shaded':
			shaded=True
			return shaded
		return shaded
	def set_occupant(self,rover):
		"""
		Sets the occupant on the terrain tile
		"""
		pass
	def get_occupant(self,rover):
		"""
		Gets the entity on the terrain tile
		If nothing is on this tile, it should return None
		"""
		pass
	def is_slope(self):
		is_slope=False
		if type(self.elevation)==list:
			is_slope=True
			return is_slope
		return is_slope
	def is_explored(self,rover):
		if self in rover.explored:
			return True
		return False
