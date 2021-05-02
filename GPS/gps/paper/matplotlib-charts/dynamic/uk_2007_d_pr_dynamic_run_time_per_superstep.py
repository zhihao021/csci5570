import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt

import sk_2005_pr_network_io_data as dataFile

rc("font", size=17)
pdf = PdfPages('uk_2007_d_pr_dynamic_run_time_per_superstep.pdf')
fig = p.figure(figsize=(6.5,4.1))
ax = fig.add_axes((0.15, 0.15, 0.75, 0.7))

xIndices = range(1, 26, 1)
print len(xIndices)
ax.plot(xIndices,
[
82.5,
90.605,
92.27,
90.442,
91.941,
91.63,
91.37,
91.56,
90.94,
91.02,
91.875,
90.5,
90.78,
91.32,
91.26,
90.85,
91.56,
91.23,
92.16,
91.44,
91.87,
91.12,
90.34,
90.67,
91.7
], label='Static')

ax.plot(xIndices,
[
89.598,
139.248,
144.764,
147.68,
142.836,
132.897,
125.696,
117.126,
116.881,
114.071,
111.705,
102.453,
91.897,
90.199,
88.74,
82.044,
81.017,
80.323,
79.78,
81.232,
81.903,
81.24,
79.902,
80.838,
81.487,
], label='Dynamic')

ax.set_xticks(range(1, 26, 2))
ax.set_yticks(range(0, 160, 20))

ax.set_ylabel('Run-time (seconds)')
ax.set_xlabel('Superstep no')
#ax.axis([0, 15, 0, 78])
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
ax.legend(loc='center', bbox_to_anchor=(0.77, 0.82))

pdf.savefig()
pdf.close()
p.show()
