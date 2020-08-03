from terrain import Tile as t
from rover import Rover as r
from planet import Planet as p

def load_level(filename):
	"""
	Loads the level and returns an object of your choosing
	"""
	tile_content=[]
	with open(filename,'r') as game:
		read=game.readlines()
		for line in read:
			line=line.strip('\n')
			if 'name' in line:
				name,true_name=line.split(',')
				tile_content.append(true_name)
			elif 'width' in line:
				width,parameter_1=line.split(',')
				no_1=int(parameter_1)
				tile_content.append(no_1)
			elif 'height' in line:
				height,parameter_2=line.split(',')
				no_2=int(parameter_2)
				tile_content.append(no_2)
			elif 'rover' in line:
				rover,x_coordinate,y_coordinate=line.split(',')
				x=int(x_coordinate)
				y=int(y_coordinate)
				tile_content.append(x)
				tile_content.append(x)
			elif 'plains' in line:
				set_for_tile=line.split(',')
				if len(set_for_tile)==2:
					terrain_type=set_for_tile[0]
					elevation=set_for_tile[1]
					tile=t(terrain_type,elevation)
					tile_content.append(tile)
				elif len(set_for_tile)==3:
					terrain_type=set_for_tile[0]
					elevation=set_for_tile[1:]
					tile=t(terrain_type,elevation)
					tile_content.append(tile)
			elif 'shaded' in line:
				set_for_tile=line.split(',')
				if len(set_for_tile)==2:
					terrain_type=set_for_tile[0]
					elevation=set_for_tile[1]
					tile=t(terrain_type,elevation)
					tile_content.append(tile)
				elif len(set_for_tile)==3:
					terrain_type=set_for_tile[0]
					elevation=set_for_tile[1:]
					tile=t(terrain_type,elevation)
					tile_content.append(tile)
	return tile_content
