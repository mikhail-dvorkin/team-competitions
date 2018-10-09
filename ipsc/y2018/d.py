#!/usr/bin/python3
import inspect
import random
import sys

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
		if s[0] in _fset:
			eval(s[0])(*s[1:])
			continue
		if s[2] in _fset:
			eval(s[2])(*s[3:], s[0])
			continue
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
			print(str3(value), '=', *map(str3, vs), command.upper())

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

def spread_right(x='x', y='y'):
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
	r(f'{y} = decreasings {x} ; {y} |= 1 ; {y} = spread_right {y} ; {y} = {all_ones()} - {y} ; count_ones {y} {y} 1')

def signum_deprecated(x='x', y='y'):
	r(f'{y} = spread_right {x} ; {y} &= 1')

def signum(x='x', y='y'):
	r(f'{y} = {x} & {all_ones()} ; {y} += 1 ; {y} = 1 % {y}')

def is_zero(x='x', y='y'):
	r(f'{y} = signum {x} ; {y} = 1 - {y}')

def eq(x='x', v=1, y='y'):
	r(f'{y} = {x} - {v} ; {y} = is_zero {y}')

def neq(x='x', v=1, y='y'):
	r(f'{y} = {x} - {v} ; {y} = signum {y}')

#v must be positive
def at_least(x='x', v=2, y='y'):
	assert x != y
	r(f'{y} = {x} % {v} ; {y} -= {x} ; {y} = signum {y}')

def divisible(x='x', v=4, y='y'):
	r(f'{y} = {x} % {v} ; {y} = is_zero {y}')

def power(x='x', exp=6, mod=100, maxx=1000000000, y='y'):
	r(f'p = {x} % {mod} ; {y} = 1 ; q = {exp}')
	for i in range(maxx.bit_length()):
		if i:
			r(f'p *= p ; p %= {mod} ; q /= 2')
		r(f'r = q % 2 ; s = p - 1 ; r *= s ; r += 1 ; {y} *= r ; {y} %= {mod}')

def is_prime(x='x', y='y', iters=10, maxx=1000000000):
	r(f'n = {x} ; p = at_least n 5 ; p = 1 - p ; p *= 4 ; n += p')
	r(f'd = n - 1')
	for _ in range(maxx.bit_length()):
		r(f'p = d % 2 ; p = 2 - p ; d /= p')
	r(f'e = n - 3 ; p = is_zero e ; e += p ; {y} = 0')
	random.seed(566)
	for _ in range(iters):
		a = random.randrange(0, MODULO)
		r(f'a = {a} % e ; a += 2')
		power('a', 'd', 'n', maxx, 'a')
		r(f'd = n - 1')
		r(f'p = neq a 1 ; b = p ; p = neq a d ; b &= p')
		for _ in range(maxx.bit_length() - 2):
			r(f'a *= a ; a %= n ; p = neq a d ; b &= p')
		r(f'{y} |= b')
	r(f'{y} = 1 - {y}')
	
	for p in [3, 5, 7]:
		r(f'a = divisible {x} {p} ; b = at_least {x} {p + 1} ; a &= b ; a = 1 - a ; {y} *= a')
	#Handle even x.
	r(f'a = {x} % 2 ; {y} *= a')
	#Handle x <= 3. a = [x >= 4]; y = a * y + (1 - a) * [x in 2, 3] 
	r(f'a = at_least {x} 4 ; b = 1 - a ; c = {x} / 2 ; b *= c ; {y} *= a ; {y} += b')

def test_many(program, ideal):
	tested = list(range(32)) + list(range(MODULO - 16, MODULO))
	for i in tested:
		assert run(program, i) == ideal(i), (program, i)

def test(thorough=False):
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
	assert run(spread_right, int('1', 3)) == int('1', 3)
	assert run(spread_right, int('100010', 3)) == int('111111', 3)
	assert run(spread_right, int('1' + '0' * 39, 3)) == int('1' * 40, 3)
	assert run(spread_right, int('0', 3)) == int('0', 3)
	assert run(count_ones, int('101' * 13, 3)) == 26
	assert run(decreasing_prefix, int('1' + '0' * 39, 3)) == 1
	assert run(decreasing_prefix, int('12' + '0' * 38, 3)) == 2
	assert run(decreasing_prefix, int('1', 3)) == TRITS
	assert run(decreasing_prefix, int('0', 3)) == TRITS
	assert run(decreasing_prefix, int('1112222001202120', 3)) == TRITS - 9
	test_many(signum, lambda x : 1 if x > 0 else 0)
	test_many(is_zero, lambda x : 1 if x == 0 else 0)
	test_many(eq, lambda x : 1 if x == 1 else 0)
	test_many(neq, lambda x : 1 if x != 1 else 0)
	test_many(at_least, lambda x : 1 if x >= 2 else 0)
	test_many(divisible, lambda x : 1 if x % 4 == 0 else 0)
	test_many(power, lambda x : x ** 6 % 100)
	if thorough:
		for i in list(range(100)) + list(range(10**9 - 100, 10**9 + 1)):
			prime = all([i % j for j in range(2, int(i ** 0.5) + 1)]) and i > 1
			assert run(is_prime, i) == int(prime)


_fset = set([name for name, obj in inspect.getmembers(sys.modules[__name__]) if inspect.isfunction(obj) and len(name) > 1])

test()
#_mode, _debug = exec, True
_mode = print
#decreasing_prefix() #D1
is_prime() #D2
