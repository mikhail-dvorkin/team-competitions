#!/usr/bin/python3

TRITS = 40
MODULO = 3 ** TRITS
OPS = {
	'+': lambda x, y: (x + y) % MODULO,
	'-': lambda x, y: (x - y) % MODULO,
	'*': lambda x, y: (x * y) % MODULO,
	'/': lambda x, y: x // y,
	'%': lambda x, y: x % y,
	'&': lambda x, y: tritwise(min, x, y),
	'|': lambda x, y: tritwise(max, x, y),
	'^': lambda x, y: tritwise(lambda a, b: (a + b) % 3, x, y),
	'<<': lambda x, y: (x * (3 ** y)) % MODULO if 0 <= y <= TRITS else 1 / 0,
	'>>': lambda x, y: x // (3 ** y) if 0 <= y <= TRITS else 1 / 0,
	'~': lambda x: tritwise(lambda a: 2 - a, x),
	None: lambda x: x,
}
DIR_0 = dict([(chr(i), 0) for i in range(ord('a'), ord('z') + 1)])
DIR = dict(DIR_0)
_mode = print
_debug = False

def tritwise(f, *x):
	ans, t = 0, 1
	for i in range(TRITS):
		lowest_trits = [a % 3 for a in x]
		x = [a // 3 for a in x]
		ans += f(*lowest_trits) * t
		t *= 3
	return ans

def str3(x):
	s = []
	tritwise(lambda a: s.append(str(a)) or 0, x)
	return ''.join(reversed(s))

def r(commands):
	for command in commands.split(' ; '):
		s = command.split()
		if s[0] in OPS:
			command = s[1] + ' = ' + s[0] + ' ' + s[1]
			s = command.split()
		if s[1] != '=':
			command = s[0] + ' = ' + s[0] + ' ' + s[1][:-1] + ' ' + s[2]
			s = command.split()
		if len(s) == 5:
			var0, _, var1, op, var2 = s
			vs = (var1, var2)
		elif len(s) == 4:
			var0, _, op, var1 = s
			vs = (var1,)
		else:
			var0, _, var1 = s
			if var0 == var1:
				continue
			vs, op = (var1,), None

		if _mode == print:
			print(command.upper())
			continue

		vs = [DIR[v] if v in DIR else int(v) for v in vs]
		value = OPS[op](*vs)
		DIR[var0] = value
		if _debug:
			print(str3(value), '=', *map(str3, vs), '|', command.upper())

def run(program, value, x='x', y='y'):
	if _mode == print:
		print()
	global DIR
	DIR = dict(DIR_0)
	r(f'{x} = {value}')
	program()
	return DIR[y]

def sample0():
	r('a = x - 1 ; b = ~ a ; c = b ^ x ; x = x & c')

def sample00():
	r('a = x - 1 ; b = ~ a ; c = b ^ x ; x &= c')

def all_ones():
	return MODULO // 2

def decreasings(x='x', y='y'):
	assert x != y
	r(f'{y} = {x} << 1 ; {y} &= {x} ; ~ {y} ; {y} ^= {x} ; ~ {y} ; {y} &= {all_ones() - 1}')

def spread_1_right(x='x', y='y'):
	assert x != 'p' != y
	r(f'p = {x} >> 1 ; {y} = {x} | p')
	i = 2
	while i < TRITS:
		r(f'p = {y} >> {i} ; {y} |= p')
		i *= 2

# Can be done in logarithmic time
def count_ones(x='x', y='y', add=0):
	assert x != 'p' != y and x != 'q' != y
	half = TRITS // 2
	low_twos = 3 ** half - 1
	r(f'p = {x} >> {half} ; {y} = {x} & {low_twos} ; p += {y} ; {y} = {add}')
	for i in range(TRITS - half - 1):
		r(f'q = p % 3 ; {y} += q ; p /= 3')
	r(f'{y} += p')

def decreasing_prefix(x='x', y='y'):
	assert x != y
	decreasings(x, y)
	r(f'{y} |= 1')
	spread_1_right(y, y)
	r(f'{y} = {all_ones()} - {y}')
	count_ones(y, y, add=1)

def test():
	global _debug, _mode
	_mode, _debug = exec, False
	assert OPS['+'](10, 100) == 110
	assert OPS['&'](int('210', 3), int('111', 3)) == int('110', 3)
	assert OPS['|'](int('210', 3), int('111', 3)) == int('211', 3)
	assert OPS['^'](int('210', 3), int('111', 3)) == int('021', 3)
	assert OPS['~'](int('210', 3)) == MODULO - int('211', 3)
	assert OPS['<<'](int('210', 3), 2) == int('21000', 3)
	assert OPS['>>'](int('210', 3), 2) == int('2', 3)
	assert run(sample0, int('12000', 3), y='x') == int('10000', 3)
	assert run(sample0, int('12100', 3), y='x') == int('12000', 3)
	assert run(sample00, int('12020', 3), y='x') == int('12000', 3)
	assert run(decreasings, int('210121020', 3)) == int('110011010', 3)
	assert run(decreasings, int('10', 3)) == int('10', 3)
	assert run(decreasings, int('0', 3)) == int('0', 3)
	assert run(decreasings, int('1', 3)) == int('0', 3)
	assert run(spread_1_right, int('1', 3)) == int('1', 3)
	assert run(spread_1_right, int('100010', 3)) == int('111111', 3)
	assert run(spread_1_right, int('1' + '0' * 39, 3)) == int('1' * 40, 3)
	assert run(spread_1_right, int('0', 3)) == int('0', 3)
	assert run(count_ones, int('101' * 13, 3)) == 26
	assert run(decreasing_prefix, int('1' + '0' * 39, 3)) == 1
	assert run(decreasing_prefix, int('12' + '0' * 38, 3)) == 2
	assert run(decreasing_prefix, int('1', 3)) == TRITS
	assert run(decreasing_prefix, int('0', 3)) == TRITS
	assert run(decreasing_prefix, int('1112222001202120', 3)) == TRITS - 9

test()
_mode = print
decreasing_prefix()
