import os, sys, inspect
cmd_folder = os.path.abspath(os.path.split(inspect.getfile( inspect.currentframe() ))[0])
if cmd_folder not in sys.path:
    sys.path.insert(0, cmd_folder)

import numpy as np
import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt
#from static.runtime import uk_2007_u_all_algorithms_run_time_data as dataFile
#from static.giraph import uk_2005_d_giraph_all_algorithms_run_time_data as dataFile
#from static.networkio import uk_2007_u_all_algorithms_network_io_data as dataFile
#from dynamic import uk_2007_d_pr_dynamic_run_time_with_local_optimum_data as dataFile
from dynamic import uk_2007_d_pr_dynamic_network_io_with_local_optimum_data as dataFile
#import uk_2007_u_rw_run_time_data as dataFile

fontSize = 17
if hasattr(dataFile, 'fontSize'):
    fontSize = dataFile.fontSize
print 'fontSize: %d' % (fontSize)

rc("font", size=fontSize)
pdf = PdfPages(dataFile.pdfPageName)
#6.2, 6.2

figureSize = (5, 6)
if hasattr(dataFile, 'figSize'):
    figureSize = dataFile.figSize
else:
    print 'No figSize specified. Setting to default: figureSize: (%d, %d)' % (figureSize[0], figureSize[1])
fig = p.figure(figsize=figureSize)
figureAxes = (0.17, 0.31, 0.75, 0.6)
if hasattr(dataFile, 'figAxes'):
    figureAxes = dataFile.figAxes

print 'figureAxes: (%.2f, %.2f, %.2f, %.2f)' % (figureAxes[0], figureAxes[1], figureAxes[2], figureAxes[3])

ax = fig.add_axes(figureAxes)
# Calculate how many bars there will be
N = len(dataFile.yValueGroups[0])
 
# Generate a list of numbers, from 0 to N
# This will serve as the (arbitrary) x-axis, which
# we will then re-label manually.
#xIndices = range(N)
xIndices = np.arange(N)

# See note below on the breakdown of this command
bars1 = ax.bar(xIndices, dataFile.yValueGroups[0], dataFile.barWidth,
               align='center', color='b')
bars2 = ax.bar(xIndices+dataFile.barWidth, dataFile.yValueGroups[1],
               dataFile.barWidth, align='center', color='0.9')
ax.set_ylim(0, dataFile.yLim)
ax.set_ylabel(dataFile.yLabel)
if hasattr(dataFile, 'xLabel'):
    ax.set_xlabel(dataFile.xLabel)
else:
    print 'No xLabel is defined. Not calling ax.set_xLabel(...)'

# This sets the ticks on the x axis to be exactly where we put
# the center of the bars1.
ax.set_xticks(xIndices + (dataFile.barWidth / 2))
ax.set_yticks(range(0, dataFile.yLim, dataFile.yTicksFrequency))

# Set the x tick labels to the group_labels defined above.
ax.set_xticklabels(dataFile.groupLabels)
# Extremely nice function to auto-rotate the x axis labels.
# It was made for dates (hence the name) but it works
# for any long x tick labels
if hasattr(dataFile, 'rotateXticks'):
    if (dataFile.rotateXticks):
        fig.autofmt_xdate()
else:
    print 'No rotateXticks defined. By default rotating the xticks'
    fig.autofmt_xdate()

if hasattr(dataFile, 'legendAnchor'):
    ax.legend((bars1[0], bars2[0]), dataFile.legendLabels, loc='center',
              bbox_to_anchor=(dataFile.legendAnchor[0],
                              dataFile.legendAnchor[1]))
else:
    print 'No legendAnchor is defined. Using default values for legend anchor'
    ax.legend((bars1[0], bars2[0]), dataFile.legendLabels, loc='center',
              bbox_to_anchor=(0.67, 0.6))

def autolabel(rects, dataFile):
    # attach some text labels
    labelFormatString = '%.1f'
    if hasattr(dataFile, 'integerBarLabels'):
        if (dataFile.integerBarLabels):
            labelFormatString = '%d'
    else:
        print 'No integerBarLabels defined. Setting to default...'
    print 'labelFormatString %s' % labelFormatString

    rotation = 'horizontal'
    if hasattr(dataFile, 'barLabelsRotation'):
        rotation = dataFile.barLabelsRotation
    else:
        print 'No barLabelsRotation defined. Setting to default...'
    print 'bar labels rotation %s' % rotation

    for rect in rects:
        height = rect.get_height()
        plt.text(rect.get_x()+rect.get_width()/2.,
                 1.05*height, labelFormatString % height,
                 ha='center', va='bottom', rotation=rotation)

autolabel(bars1, dataFile)
autolabel(bars2, dataFile)
pdf.savefig()
pdf.close()
p.show()
