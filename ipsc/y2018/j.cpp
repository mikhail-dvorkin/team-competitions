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

const int D = 8;

int dx[] = {1, 0, -1, 0, 1, -1, -1, 1};
int dy[] = {0, 1, 0, -1, 1, 1, -1, -1};

vector<pii> ans;

int n, m;
bool ok(int x, int y) {
    return min(x, y) >= 0 && min(n - x, m - y) > 0;
}

bool rec(vector<pii> &p, int x, int y, int mask) {
//    cerr << p.size() << ' ' << x << ' ' << y << ' ' << mask << '\n';
    if (mask == (1 << D) - 1) {
        ans = p;
        return true;
    }
    forn(d, D) {
        if ((mask >> d) & 1) continue;
        for (int xx = x + dx[d], yy = y + dy[d]; ok(xx, yy); xx += dx[d], yy += dy[d]) {
            bool ok = true;
            forn(i, p.size() - 1) {
                int dx = abs(xx - p[i].fi), dy = abs(yy - p[i].se);
                if (dx > dy) swap(dx, dy);
                if (dx == 0 || dx == dy) ok = false;
            }
            if (!ok) continue;
            p.pb({xx, yy});
            if (rec(p, xx, yy, mask | (1 << d))) return true;
            p.pop_back();
        }
    }
    return false;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.precision(10);
    cout << fixed;

//    cin >> n >> m;
    int S;
    for (S = 1; ; ++S) {
        cerr << S << '\n';
        for1(n, S) {
            if (S % n) continue;
            ::n = n;
            ::m = S / n;
            forn(i, n) forn(j, m) {
                vector<pii> p = {{i, j}};
                if (rec(p, i, j, 0)) {
                    for (auto w: p) cerr << w.fi << ' ' << w.se << '\n';
                    vector<string> f(n, string(m, '#'));
                    for (auto p: ans) f[p.fi][p.se] = '.';
                    f[ans[0].fi][ans[0].se] = 'A';
                    f[ans.back().fi][ans.back().se] = 'B';
                    cout << n << ' ' << m << '\n';
                    forn(i, n) cout << f[i] << '\n';
                    return 0;
                }
            }
        }
    }

#ifdef LOCAL_DEFINE
    cerr << "Time elapsed: " << 1.0 * clock() / CLOCKS_PER_SEC << " s.\n";
#endif
    return 0;
}
