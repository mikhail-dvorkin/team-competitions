import numpy as np
import random
import sys

def egcd(a, b):
    if a == 0:
        return (b, 0, 1)
    else:
        g, y, x = egcd(b % a, a)
        return (g, x - (b // a) * y, y)

def modinv(a, m):
    g, x, y = egcd(a, m)
    if g != 1:
        raise Exception('modular inverse does not exist')
    else:
        return x % m


tests = int(input())

for test in range(tests):
    input()
    p, k, n = map(int, input().split(' '))
    as_ = []
    bs = []
    invas = []
    invbs = []
    ms = []
    k0s = []
    rates = []
    for i in range(n):
        a, b, m = map(int, input().split(' '))
        as_.append(a)
        invas.append(modinv(a, p))
        bs.append(b)
        invbs.append((p - b) % p)
        ms.append(m)
        k0 = (k * a + b) % p % m
        k0s.append(k0)
        # r*m < p - k0
        rates.append((p - k0 + m - 1) // m)
    rs = sum(rates)
    samples = 10000
    sum_inv_cnts = 0.
    sum_inv_cnts_2 = 0.
    cnts = []
    while True:
        for i in np.random.choice(range(n), samples, p=np.array([r / rs for r in rates])):
            k0 = k0s[i]
            m = ms[i]
            max_mul = (p - k0 + m - 1) // m
            x = k0 + m * random.randint(0, max_mul - 1)
            x = (invas[i] * (x + invbs[i])) % p
            cnt = 0
            for j in range(n):
                if (x * as_[j] + bs[j]) % p % ms[j] == k0s[j]:
                    cnt += 1
            cnts.append(1.0 / cnt)
        mean = np.mean(cnts)
        sigma = np.std(cnts) / np.sqrt(len(cnts))
        if sigma / mean > 0.01 / 5:
            continue
        print(rs * mean)
        print(test, len(cnts) // samples, mean, sigma, sigma / mean, file=sys.stderr)
        break
