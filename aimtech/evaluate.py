#!/usr/bin/env python
from res import aimmaze

def main():
	aimmaze.CELL_SIZE = 4
	aimmaze.WALL_WIDTH = 1
	aimmaze.SOLUTION_WIDTH = 1
	aimmaze.ROBOT_COLOR = "blue"
	aimmaze.WALL_COLOR = "gray"
	aimmaze.GRID_COLOR = "white"
	aimmaze.KEY_COLOR = "red"

	their_quit_ok = aimmaze.quit_ok

	def my_quit_ok(points, text):
	    #assert(points >= 0 and points <= 1e18)
	    #print("points {:.4f} {}".format(points, text), file=sys.stderr)
	    #sys.exit(7)
	    their_quit_ok(points, text)

	aimmaze.quit_ok = my_quit_ok

	aimmaze.main()


if __name__ == '__main__':
	main()
