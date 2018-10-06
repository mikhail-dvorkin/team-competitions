#include <iostream>
#include <tuple>
#include <sstream>
#include <vector>
#include <cmath>
#include <ctime>
#include <bitset>
#include <cassert>
#include <cstdio>
#include <queue>
#include <set>
#include <map>
#include <fstream>
#include <cstdlib>
#include <string>
#include <cstring>
#include <algorithm>
#include <numeric>

#define mp make_pair
#define mt make_tuple
#define fi first
#define se second
#define pb push_back
#define all(x) (x).begin(), (x).end()
#define rall(x) (x).rbegin(), (x).rend()
#define forn(i, n) for (int i = 0; i < (int)(n); ++i)
#define for1(i, n) for (int i = 1; i <= (int)(n); ++i)
#define ford(i, n) for (int i = (int)(n) - 1; i >= 0; --i)
#define fore(i, a, b) for (int i = (int)(a); i <= (int)(b); ++i)

using namespace std;

typedef pair<int, int> pii;
typedef vector<int> vi;
typedef vector<pii> vpi;
typedef vector<vi> vvi;
typedef long long i64;
typedef vector<i64> vi64;
typedef vector<vi64> vvi64;
typedef pair<i64, i64> pi64;
typedef double ld;

template<class T> bool uin(T &a, T b) { return a > b ? (a = b, true) : false; }
template<class T> bool uax(T &a, T b) { return a < b ? (a = b, true) : false; }

const int maxn = 5010;
const int K = 2;
const i64 P = 1000000000 + 7;
//i64 dp[maxn][maxn];
//i64 fact[K * maxn], tcaf[K * maxn];
i64 pe[K * maxn];
i64 pcnk[K * maxn][K * maxn];
vi fact, inv;

i64 deg(i64 x, i64 d) {
    if (d < 0) d += P - 1;
    d %= P - 1;
    i64 y = 1;
    while (d) {
        if (d & 1) (y *= x) %= P;
        (x *= x) %= P;
        d /= 2;
    }
    return y;
}

long long FactDivP(long long N, long long P = ::P) {
    long long res = 0;
    N /= P;
    while (N > 0) {
        res += N;
        N /= P;
    }
    return res;
}

long long FactGModP(long long N, bool isinv = false, long long P = ::P) {
    if (N == 0) {
        return 1;
    }
    long long res;
    if ((N / P) % 2 == 0) {
        res = 1;
    } else {
        res = P - 1;
    }
    res = isinv ? inv[N % P] : fact[N % P];
    return (res * FactGModP(N / P, isinv, P)) % P;
}

long long CnkModP(long long N, long long K, long long P = ::P) {
    if (K < 0 || K > N) return 0;
//    assert(N < P && K < P && K <= N);
    return 1LL * fact[N] * inv[N - K] % P;
    if (FactDivP(N, P) > FactDivP(K, P) + FactDivP(N - K, P)) {
        return 0;
    }

    long long res = FactGModP(N);
    res *= FactGModP(K, 1); res %= P;
    res *= FactGModP(N - K, 1); res %= P;
    return res;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.precision(10);
    cout << fixed;
#ifdef LOCAL_DEFINE
    freopen("input.txt", "rt", stdin);
#endif

    fact.resize(P);
    fact[0] = 1;
//    for1(i, K * maxn - 1) fact[i] = i * fact[i - 1] % P;
//    forn(i, K * maxn) tcaf[i] = deg(fact[i], -1);
    for1(i, P - 1) fact[i] = 1LL * fact[i - 1] * i % P;
    inv.resize(P);
    inv[1] = 1;
    fore(i, 2, P - 1) inv[i] = -1LL * inv[P % i] * (P / i) % P;
    inv[0] = 1;
    for1(i, P - 1) inv[i] = 1LL * inv[i] * inv[i - 1] % P;

    //pe[0] = 1;
    //for1(i, K * maxn - 1) (pe[i] = pe[i - 1] + 1LL * deg(fact[i], -1) * (i % 2 ? P-1 : 1)) %= P;
    pcnk[0][0] = 1;
    forn(i, K * maxn - 1) forn(j, i + 1) forn(k, 2) (pcnk[i + 1][j + k] += pcnk[i][j]) %= P;
    for1(i, K * maxn - 1) forn(j, i + 1) {
        if ((i - j) % 2) pcnk[i][j] = -pcnk[i][j];
        (pcnk[i][j] += pcnk[i - 1][j]) %= P;
    }

    int N;
    cin >> N;
    for1(n, N) {
        cerr << n << '\n';
        i64 S = 0;
        for1(i, K * n) {
            i64 z = pcnk[K * n][i];
            i64 res = 0;
            for1(j, i) {
                i64 t = 1LL * i * (i - 1) * j * (j - 1) / 4 % P;
                if (t < n) continue;
                i64 v = CnkModP(t, n) * pcnk[K * n][j];
                if (j < i) v *= 2;
                (res += v) %= P;
            }
            (S += res * z) %= P;
        }

        (S *= inv[n]) %= P;
        if (S < 0) S += P;
        cout << S << '\n';
    }

#ifdef LOCAL_DEFINE
    cerr << "Time elapsed: " << 1.0 * clock() / CLOCKS_PER_SEC << " s.\n";
#endif
    return 0;
}
