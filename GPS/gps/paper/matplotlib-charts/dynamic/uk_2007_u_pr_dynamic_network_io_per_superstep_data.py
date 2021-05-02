import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt

import sk_2005_pr_network_io_data as dataFile

rc("font", size=18)
pdf = PdfPages('uk_2007_u_pr_dynamic_network_io_per_superstep.pdf')
fig = p.figure(figsize=(7,5))
ax = fig.add_axes((0.15, 0.15, 0.75, 0.7))

xIndices = range(1, 16, 1)
print len(xIndices)
ax.plot(xIndices,
        [
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226,
8.46669713035226
         ], label='Static')
ax.plot(xIndices,
[
8.46669713035226,
8.46669713035226,
10.0702145136893,
7.613864723593,
8.57039698585868,
6.33079632371664,
5.86767908744514,
5.51712149381638,
5.3086059782654,
5.00258765742183,
4.95316250622272,
4.76614960469306,
4.66154656559229,
4.66154656559229,
4.66154656559229,
],
 label='Dynamic')

ax.set_xticks(xIndices)
ax.set_yticks(range(0, 12, 1))

ax.set_ylabel('Network I/O (GB)')
ax.set_xlabel('Superstep no')
#ax.axis([0, 15, 0, 78])
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
ax.legend(loc='center', bbox_to_anchor=(0.8, 0.66))

pdf.savefig()
pdf.close()
p.show()
