tests = int(input())

mod = 1000000007

dirs = 'TBLR'

def inv(c):
    return dirs[dirs.index(c) ^ 1]

for test in range(tests):
    input()
    n = int(input())
    folds = input()
    cut = input()

    adds = 0
    cuts = {cut: 1}
    for f in folds[::-1]:
        adds = (2 * adds) % mod
        new_cuts = {}
        def add(cut, cnt):
            if cut not in new_cuts:
                new_cuts[cut] = 0
            new_cuts[cut] = (new_cuts[cut] + cnt) % mod
        def mirror(c):
            return inv(c) if c == inv(f) else c
        for cut, cnt in cuts.items():
            if f in cut:
                if cut == f + f:
                    adds = (adds + cnt) % mod
                else:
                    f1 = cut[1] if cut[0] == f else cut[0]
                    if f1 == inv(f):
                        add(cut, cnt)
                    else:
                        add(f1 + f1, cnt)
            else:
                add(cut, cnt)
                add(''.join(map(mirror, cut)), cnt)
        cuts = new_cuts
    ans = (1 + adds) % mod
    for cut, cnt in cuts.items():
        ans = (ans + cnt) % mod
    print(ans)
