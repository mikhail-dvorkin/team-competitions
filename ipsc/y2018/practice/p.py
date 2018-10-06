#!/usr/bin/python3
import os.path

def solve():
	n = int(fin.readline())
	m = 1
	while m ** 2 % n:
		m += 1
	ans = []
	for i in range(m):
		a = []
		for j in range(m):
			a.append(chr(ord('a') + (i * m + j) // (m ** 2 // n)))
		ans.append(''.join(a))
	return '\n'.join([str(m)] + ans)


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
