#!/usr/bin/python3
import os.path

def solve(easy, max_len=25, easy_len=4, easy_max=2):
	n = int(fin.readline())
	a = fin.readline().split()
	ds = []
	names = []
	for i in range(n):
		k = i + 1
		if a[i].isdigit():
			if a[i] != str(k):
				return i
			for d in ds:
				if k % d == 0:
					return i
		else:
			prefix = []
			for j in range(len(ds)):
				if k % ds[j] == 0:
					prefix.append(names[j])
			prefix = ''.join(prefix)
			if not a[i].startswith(prefix):
				return i
			s = a[i][len(prefix):]
			if not s:
				continue
			if not (1 <= len(s) <= max_len) or not s.isalpha():
				return i
			if easy:
				if len(s) != easy_len or len(ds) == easy_max:
					return i
			ds.append(k)
			names.append(s)
	return n


filename = os.path.splitext(os.path.basename(__file__))[0].split('_')[0].lower()
for suffix in '012':
	fin = open(filename + suffix + '.in')
	fout = open(filename + suffix + '.out', 'w')

	tests = int(fin.readline())
	for t in range(tests):
		fin.readline()
		easy = suffix == '1'
		ans = str(solve(easy))
		print(ans)
		fout.write(ans + '\n')

	fin.close()
	fout.close()
