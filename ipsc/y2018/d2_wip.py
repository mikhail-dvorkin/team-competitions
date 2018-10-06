#!/usr/bin/python3
import random

random.seed(566)

def checkPrime(p):
	print("A = X % " + str(p))
	print("T = A >> 1")
	print("A = A | T")
	print("T = A >> 2")
	print("A = A | T")
	print("T = A >> 4")
	print("A = A | T")
	print("A = A & 1")
	print("Y = Y & A")

def div2():
	print("R = D % 2")
	print("R = 2 - R")
	print("D = D / R")

def findD():
	print("D = X - 1")
	for i in range(32):
		div2()

def findB():
	print("B = X - 5")
	# !!!!!!!!!!

def power():
	print("C = 1")
	print("E = D")
	print("F = A")
	
	print("A = A % X")

def checkNonZero():
	'''P != 0 -> P == 1'''
	for i in [1, 2, 4, 8, 16, 32]:
		print('Q = P >> ' + str(i))
		print('P = P | Q')
	print('P = P & 1')

def miller():
	r = random.randint(0, 3 ** 40 - 1)
	print("A = " + str(r) + " % B")
	print("A = A + 2")
	power()
	print("T = 1")
	print("P = A - 1")
	checkNonZero()
	print("T = T & P")

	print("P = X - 1")
	print("P = P - A")
	checkNonZero()
	print("T = T & P")

print("Y = 1")
for i in range(2, 30):
	prime = True
	for j in range(2, i):
		if i % j == 0:
			prime = False
	if not prime:
		continue
	checkPrime(i)
findD()
findB()
miller()
