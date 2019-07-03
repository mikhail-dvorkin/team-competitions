#!/usr/bin/env python3
from res import aimmaze
import math
import sys


def my_quit_ok(points, text):
	_, exit_found, _, made_moves, _, made_ops, _, code_length, _, raw_score, _, maze_weight = text.split()
	if exit_found != str(True):
		text = 'Exit not found'
	else:
		brevity = round(100 * coefs[1] / sum(coefs))
		parts = [int(maze_weight) * c for c in coefs]
		text = f'OUR={points:.3f}={parts[0]:.0f}+{parts[1]:.0f}\tMOVES={made_moves}\tOPS={made_ops}\tCODE={code_length}\tBREV={brevity}%'
	print(text, file=sys.stderr)
	sys.exit(7)

def my_log(x):
	coefs.append(saved_log(x))
	return saved_log(x)

saved_log = math.log2
coefs = []

def main():
	aimmaze.CELL_SIZE = 4
	aimmaze.WALL_WIDTH = 1
	aimmaze.SOLUTION_WIDTH = 1
	aimmaze.ROBOT_COLOR = "blue"
	aimmaze.WALL_COLOR = "gray"
	aimmaze.GRID_COLOR = "white"
	aimmaze.KEY_COLOR = "red"
	aimmaze.quit_ok = my_quit_ok
	math.log2 = my_log
	aimmaze.main()


if __name__ == '__main__':
	main()
