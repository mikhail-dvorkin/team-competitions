#!/usr/bin/python3
import os.path

def solve():
	n = int(fin.readline())
	a = list(map(int, fin.readline().split()))
	s = ""
	for i in range(n - 1):
		x = 1
		while x * 10 <= a[i]:
			x *= 10
		s += str(x) + ' '
	return s + '1'


filename = os.path.splitext(os.path.basename(__file__))[0].split('_')[0].lower()
for suffix in '012':
	fin = open(filename + suffix + '.in')
	fout = open(filename + suffix + '.out', 'w')

	tests = int(fin.readline())
	for t in range(tests):
		fin.readline()
		ans = str(solve())
		print(ans)
		fout.write(ans + '\n')

	fin.close()
	fout.close()
