__author__ = 'tanunia'

import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages

names= ["b_should_be_easy.in", "c_no_hurry.in", "d_metropolis.in", "e_high_bonus.in"]
figs = []
for name in names:
    filename = name

    ind = []
    dist = []
    time_dist = []
    with open(filename, "r") as fin:
        [r, c, f, n, b, t] = [int(xx) for xx in fin.readline().strip().split()]
        for ln in fin.readlines():
            [x1, y1, x2, y2, l1, l2] = [int(xx) for xx in ln.strip().split()]
            dist.append(abs(x1-x2) + abs(y1 - y2))
            time_dist.append(l2 - l1)
        fig1 = plt.figure()
        plt.hist(dist)
        plt.title(filename + " Dist distribution")

        fig2 = plt.figure()
        plt.hist(time_dist)
        plt.title(filename + " Time range distribution")

        figs.append(fig1)
        figs.append(fig2)


pp = PdfPages('input_stats.pdf')
for fig in figs:
    pp.savefig(fig)

pp.close()

