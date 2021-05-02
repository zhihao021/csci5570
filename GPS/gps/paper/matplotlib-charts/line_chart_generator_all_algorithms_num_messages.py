import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt

import sk_2005_pr_network_io_data as dataFile

rc("font", size=18)
pdf = PdfPages('uk_2007_u_all_algorithms_num_messages_per_superstep.pdf')
fig = p.figure(figsize=(7,5))
ax = fig.add_axes((0.15, 0.15, 0.75, 0.7))

xIndices = range(1, 16, 1)
ax.plot(xIndices,
        [
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811,
6.625316811
], label='PR')

ax.plot(xIndices,
[
6.625316811,
6.34317771,
6.40498783,
6.106112414,
6.074539275,
3.968716317,
3.498074037,
2.386653506,
0.612728294,
0.11233818,
0.041004339,
0.013291461,
0.003418623,
0.001569888,
0.000658188
], label='HCC')

ax.plot(xIndices,
[
0.000000001,
0.000000027,
0.000003633,
0.000284472,
0.031995662,
1.003478304,
2.64666545,
2.21919394,
0.55975085,
0.097308961,
0.035084417,
0.010803918,
0.002481668,
0.00113362,
0.000386822
], label='SSSP')

ax.plot(xIndices,
[
3.774343088,
4.75270291,
5.157816623,
5.299431678,
5.396162411,
5.458591076,
5.4898054085,
5.50541257475,
5.513216157875,
5.5171179494375,
5.51906884521875,
5.52004429310938,
5.52053201705469,
5.52077587902734,
5.52089781001367
], label='RW-800')

ax.set_xticks(xIndices)
ax.set_yticks(range(0, 8, 1))

ax.set_ylabel('# Messages (in billions)')
ax.set_xlabel('Superstep no')
#ax.axis([0, 15, 0, 78])
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
ax.legend(loc='center', bbox_to_anchor=(0.8, 0.50))

#ax.set_position([box.x0, box.y0, box.width * 0.93, box.height])
#ax.legend(loc='center', bbox_to_anchor=(0.8, 0.66))

pdf.savefig()
pdf.close()
p.show()
