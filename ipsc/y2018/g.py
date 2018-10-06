import zlib
# from sympy import *

mod = 1000000007

# p0, pl, ph, ps = symbols('p0 pl ph ps', integer=True)

# f3a57af828f277d9cab8453e4d4778b33d4f5906 refs/remotes/origin/example
root = '7c7837741bc79e997484dc56709ef07397d1ee60'

# 5b99b65eafacf910586dd1f50483fd2147f9988d refs/remotes/origin/easy
# root = '3636ca1b8b1d14cc8a0fb3746a86912cb5c3fc5a'

# 5d0ecb09c299182e46e0b27211bfc9b8d5129ab4 refs/remotes/origin/hard
# root = '991ec2729aba5ad64b86df2e0f9f241f2376ea68'

class Poly:
    def __init__(self, c=0, p0=0, pl=0, ph=0, ps=0, p0ps=0, plps=0):
        self.c = c
        self.p0 = p0
        self.pl = pl
        self.ph = ph
        self.ps = ps
        self.p0ps = p0ps
        self.plps = plps

    def __add__(self, other):
        r = Poly()
        r.c = (self.c + other.c) % mod
        r.p0 = (self.p0 + other.p0) % mod
        r.pl = (self.pl + other.pl) % mod
        r.ph = (self.ph + other.ph) % mod
        r.ps = (self.ps + other.ps) % mod
        r.p0ps = (self.p0ps + other.p0ps) % mod
        r.plps = (self.plps + other.plps) % mod
        return r

    def __mul__(self, other):
        r = Poly()
        r.c = (self.c * other) % mod
        r.p0 = (self.p0 * other) % mod
        r.pl = (self.pl * other) % mod
        r.ph = (self.ph * other) % mod
        r.ps = (self.ps * other) % mod
        r.p0ps = (self.p0ps * other) % mod
        r.plps = (self.plps * other) % mod
        return r

    def subs(self, new_ps, new_pl, new_ph, new_p0):
        assert self.p0ps == 0 or (new_p0.ph == 0 and new_p0.ps == 0 and new_p0.p0ps == 0 and new_p0.plps == 0)
        assert self.plps == 0 or (new_pl.ph == 0 and new_pl.ps == 0 and new_pl.p0ps == 0 and new_pl.plps == 0)
        assert (self.p0ps == 0 and self.plps == 0) or \
            (new_ps.p0 == 0 and new_ps.pl == 0 and new_pl.ph == 0 and new_pl.p0ps == 0 and new_pl.plps == 0)
        r = Poly(
            c=(self.c + self.p0ps * new_p0.c * new_ps.c + self.plps * new_pl.c * new_ps.c) % mod,
            ps=(self.p0ps * new_p0.c * new_ps.ps + self.plps * new_pl.c * new_ps.ps) % mod,
            p0ps=(self.p0ps * new_p0.p0 * new_ps.ps + self.plps * new_pl.p0 * new_ps.ps) % mod,
            plps=(self.p0ps * new_p0.pl * new_ps.ps + self.plps * new_pl.pl * new_ps.ps) % mod,
            p0=(self.p0ps * new_p0.p0 * new_ps.c + self.plps * new_pl.p0 * new_ps.c) % mod,
            pl=(self.p0ps * new_p0.pl * new_ps.c + self.plps * new_pl.pl * new_ps.c) % mod,
        )
        r += new_p0 * self.p0
        r += new_pl * self.pl
        r += new_ph * self.ph
        r += new_ps * self.ps
        return r


def read(r):
    data = zlib.decompress(open('.git/objects/{}/{}'.format(r[:2], r[2:]), 'rb').read())
    i = 0
    def read_until(c):
        nonlocal i
        j = i
        while data[j] != c:
            j += 1
        ret = data[i:j]
        i = j + 1
        return ret
    read_until(0)
    while i < len(data):
        mode = read_until(32)
        name = read_until(0)
        hash = data[i:i+20]
        i += 20
        yield (name, mode, hash.hex())

cache = {}

def parseTree(r):
    if r in cache:
        return cache[r]
    i = Poly(p0=1)
    ret = Poly()
    for name, mode, hash in read(r):
        if mode == b'40000':
            # dir
            subtree_i, subtree_ret = parseTree(hash)
            new_pl = Poly(pl=1)
            new_ph = Poly(ph=1)
            new_ps = Poly(ps=1)
            for c in name + b'/':
                new_ps.c = (new_ps.c + c) % mod
                new_ph = new_ph + new_pl * c
                new_pl.c = (new_pl.c + 1) % mod
            # print(name, subtree_i.__dict__, subtree_ret.__dict__, hash, i.__dict__, new_pl.__dict__, new_ph.__dict__, new_ps.__dict__)
            subtree_i = subtree_i.subs(new_ps=new_ps, new_pl=new_pl, new_p0=i, new_ph=new_ph)
            subtree_ret = subtree_ret.subs(new_ps=new_ps, new_pl=new_pl, new_p0=i, new_ph=new_ph)
            # print(subtree_i.__dict__, subtree_ret.__dict__)
            i = subtree_i
            ret = ret + subtree_ret
        elif mode == b'100644':
            # file
            ret.ph = (ret.ph + 1) % mod
            ret.p0ps = (ret.p0ps + i.p0) % mod
            ret.plps = (ret.plps + i.pl) % mod
            ret.ps = (ret.ps + i.c) % mod
            # ret = ret + ph + i * ps
            i.pl = (i.pl + 1) % mod
            # i = i + pl
            for c in name + b'\x0A':
                ret = ret + i * c
                i.c = (i.c + 1) % mod
                # i = i + 1
            # print(name, mode, hash)
        else:
            raise Exception('Unknown mode: {}'.format(mode))
    # return (simplify(i % mod), simplify(ret % mod))
    cache[r] = (i, ret)
    if len(cache) % 1000 == 0:
        print(len(cache))
    return cache[r]

ans = parseTree(root)
print(ans[0].__dict__, ans[1].__dict__)
print((ans[1].c + ans[1].p0) % mod)
