import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt

import sk_2005_pr_network_io_data as dataFile

rc("font", size=17)
pdf = PdfPages('sk_2004_d_pr_dynamic_network_io_per_superstep.pdf')
fig = p.figure(figsize=(6.5,4.1))
ax = fig.add_axes((0.15, 0.15, 0.75, 0.7))

xIndices = range(1, 21, 1)

ax.plot(xIndices,
[
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245,
21.0970338620245
], label='Static')

ax.plot(xIndices,
[
21.0970338620245,
21.0970338620245,
23.0787778571248,
21.8492613472044,
22.6515435073525,
20.4437612481415,
19.2603807896376,
16.5866534430534,
15.5401917025447,
13.2478303052485,
12.5541112683713,
11.3780577853322,
10.8362530339509,
10.2805289309472,
9.97310292720795,
9.97310292720795,
9.97310292720795,
9.97310292720795,
9.97310292720795,
9.97310292720795],
 label='Dynamic')

ax.set_xticks(range(1, 21, 2))
ax.set_yticks(range(0, 25, 2))

ax.set_ylabel('Network I/O (GB)')
ax.set_xlabel('Superstep no')
#ax.axis([0, 15, 0, 78])
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
ax.legend(loc='center', bbox_to_anchor=(0.77, 0.7))

pdf.savefig()
pdf.close()
p.show()
