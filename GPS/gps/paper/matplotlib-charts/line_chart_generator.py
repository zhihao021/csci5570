import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt

import sk_2005_pr_network_io_data as dataFile

rc("font", size=18)
pdf = PdfPages('uk_2007_u_pr_network_io_per_superstep_with_rw_800.pdf')
fig = p.figure(figsize=(7,5))
ax = fig.add_axes((0.15, 0.15, 0.75, 0.7))

xIndices = range(1, 16, 1)
ax.plot(xIndices,
        [72.9,72.9,72.9,72.9,72.9,
         72.9,72.9,72.9,72.9,72.9,
         72.9,72.9,72.9,72.9,72.9], label='PR')
ax.plot(xIndices,
        [48.4718383103609,
         46.1877245530486,
         47.2044904604554,
         46.5405067354441,
         46.8509875461459,
         32.5956885069609,
         15.0125979632139,
         2.73237445950508,
         0.674396619200706,
         0.205661199986935,
         0.064103253185749,
         0.021575301885605,
         0.007383592426777,
         0.003471702337265,
         0.002176187932491], label='HCC')

ax.plot(xIndices,
[0.000000014901161,
0.000000357627869,
0.000003911554813,
0.014553047716618,
1.14120222628117,
6.68394085019827,
22.645738966763,
12.6762842461467,
4.01259982585907,
0.770876035094261,
0.279021814465523,
0.096804291009903,
0.019756816327572,
0.005060039460659,
0.002904951572418], label='SSSP')

ax.plot(xIndices,
[
30.6651446446776,
38.8035667985678,
41.6354357302189,
42.4514801725745,
43.0267834588885,
43.3637203648686,
43.652368709445,
43.7966928817332,
43.8688549678773,
43.9049360109493,
43.9229765324853,
43.9319967932534,
43.9365069236374,
43.9387619888294,
43.9398895214254,
], label='RW-800')

ax.set_xticks(xIndices)
ax.set_yticks(range(0, 80, 10))

ax.set_ylabel('Network I/O (GB)')
ax.set_xlabel('Superstep no')
#ax.axis([0, 15, 0, 78])
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
ax.legend(loc='center', bbox_to_anchor=(0.8, 0.30), prop={'size':17})

#ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
#ax.legend(loc='center', bbox_to_anchor=(0.8, 0.66))

pdf.savefig()
pdf.close()
p.show()
