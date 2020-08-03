from terrain import Tile as t
from rover import Rover as r
from planet import Planet as p
from loader import *
import os
import sys

def quit():
	"""
	Will quit the program
	"""
	exit()
	
def menu_help():
	"""
	Displays the help menu of the game
	"""
	pass
	print()
	print('START <level file> - Starts the game with a provided file.')
	print('QUIT - Quits the game')
	print('HELP - Shows this message')
	print()
def menu_start_game(filepath):
	"""
	Will start the game with the given file path
	"""
	pass
	
def menu():
	"""
	Start the menu component of the game
	"""
	pass

while True:
	command=input()
	if command=='QUIT':
		quit()
	elif command=='HELP':
		menu_help()
	elif 'START' in command:
		START,file=command.split()
		if os.path.exists(file)!=True:
			print()
			print('Level file could not be found')
			print()
			break
		else:
			try:
				load_level(file)
			except ValueError:
				print()
				print('Unable to load level file')
				print()
				break
			if load_level(file)[1]*load_level(file)[2]!=len(load_level(file)[5:]) or load_level(file)[0].isnumeric()==True or load_level(file)[1]<0 or load_level(file)[2]<0 or load_level(file)[3]<0 or load_level(file)[4]<0:
				print()
				print('Unable to load level file')
				print()
				break
			else:
				name=load_level(file)[0]
				width=load_level(file)[1]
				length=load_level(file)[2]
				x=load_level(file)[3]
				y=load_level(file)[4]
				tiles=load_level(file)[5:]
				position=[x,y]
				planet=p(name,width,length)
				rover=r(position,100)
				while True:
					game_command=input()
					if game_command=='FINISH':
						print()
						print('You explored {}% of {}'.format(rover.calculate_explored(planet,tiles),planet.name))
						print()
					elif 'SCAN' in game_command:
						Scan,Mode=game_command.split()
						if Mode=='shade':
							print()
							for line in rover.Scan('shade',planet,tiles):
								print(line)
							print()
						elif Mode=='elevation':
							print()
							for line in rover.Scan('elevation',planet,tiles):
								print(line)
							print()
					elif 'MOVE' in game_command:
						Move,direction,cycle=game_command.split()
						rover.move(direction,cycle,planet,tiles)
					elif game_command=='STATS':
						print()
						print('Explored: {}%'.format(rover.calculate_explored(planet,tiles)))
						print('Battery: {}/100'.format(rover.energy))
						print()
					elif game_command=='QUIT':
						quit()
					elif 'WAIT' in game_command:
						wait,cycle=game_command.split()
						rover.wait(cycle,planet,tiles)
	else:
		print()
		print('No menu item')
		print()
				
