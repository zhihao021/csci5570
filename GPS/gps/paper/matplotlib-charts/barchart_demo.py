
#!/usr/bin/env python
# a bar plot with errorbars
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages

pdf = PdfPages('barcharg_demo_pdf.pdf')

N = 5
menMeans = (44.2, 21.0, 0.8, 1.6, 1.3)
#menStd =   (2, 3, 4, 1, 2)

ind = np.arange(N)  # the x locations for the groups
width = 0.85       # the width of the bars

fig = plt.figure()
ax = fig.add_subplot(111)
rects1 = ax.bar(ind, menMeans, width, color='r')

womenMeans = (44.2, 21.0, 0.8, 1.6, 1.3)
#womenStd =   (3, 5, 2, 3, 3)
#rects2 = ax.bar(ind+width, womenMeans, width, color='y')

ax.set_ylim(0, 50)
# add some
ax.set_ylabel('Scores')
ax.set_title('Network I/O across different static partitioning')
ax.set_xticks(ind+width)
ax.set_xticklabels( ('Random', 'Domain', 'Metis-balanced', 'Metis-default', 'Metis-balanced-2') )

#ax.legend( (rects1[0]), ('Random') )
#ax.legend( (rects1[0], rects2[0]), ('Men', 'Women') )

def autolabel(rects):
    # attach some text labels
    for rect in rects:
        height = rect.get_height()
        ax.text(rect.get_x()+rect.get_width()/2., 1.05*height, '%d'%int(height),
                ha='center', va='bottom')

autolabel(rects1)
#autolabel(rects2)

pdf.savefig()
pdf.close()

#savefig(pdf, format='pdf')

#plt.show()
