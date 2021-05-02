import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt

import sk_2005_pr_network_io_data as dataFile

rc("font", size=17)
pdf = PdfPages('uk_2007_d_pr_dynamic_network_io_per_superstep.pdf')
fig = p.figure(figsize=(6.5,4.1))
ax = fig.add_axes((0.15, 0.15, 0.75, 0.7))

xIndices = range(1, 26, 1)

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
91.7,
], label='Static')

ax.plot(xIndices,
[
89.598,
144.248,
149.764,
160.68,
157.836,
137.897,
130.696,
114.126,
117.881,
116.071,
120.705,
102.453,
94.897,
92.199,
94.74,
82.044,
81.017,
80.323,
79.78,
81.232,
81.903,
82.24,
79.902,
80.838,
81.487,
], label='Dynamic')

ax.set_xticks(range(1, 26, 2))
ax.set_yticks(range(0, 170, 20))

ax.set_ylabel('Run-time (seconds)')
ax.set_xlabel('Superstep no')
#ax.axis([0, 15, 0, 78])
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
ax.legend(loc='center', bbox_to_anchor=(0.77, 0.7))

pdf.savefig()
pdf.close()
p.show()
