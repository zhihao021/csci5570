import os, sys, inspect
cmd_folder = os.path.abspath(os.path.split(inspect.getfile( inspect.currentframe() ))[0])
if cmd_folder not in sys.path:
    sys.path.insert(0, cmd_folder)

import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt
#from static.networkio import sk_2005_pr_network_io_data as dataFile
#import sk_2005_pr_run_time_60_m_60_w_METIS_default_data as dataFile
#import uk_2007_pr_network_io_data as dataFile
#from static.runtime import sk_2005_pr_run_time_60_m_60_w_data as dataFile
#from static.workload import sk_2005_pr_run_time_20_machines as dataFile
#from static.lalp import twitter_lalp_run_time_data as dataFile
#from static.lalp import twitter_lalp_network_io_data as dataFile
#from static.workload import sk_2005_pr_workload_60_m_60_w_data as dataFile 
from static.workload import sk_2005_pr_workload_data as dataFile
#from /Users/semihsalihoglu/Desktop/research/databases/gps/poster/poster-matplotlib-charts import sk_2005_pr_network_io_data_simple as dataFile

rc("font", size=17)
pdf = PdfPages(dataFile.pdfPageName)

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
N = len(dataFile.yValues)
 
# Generate a list of numbers, from 0 to N
# This will serve as the (arbitrary) x-axis, which
# we will then re-label manually.
xIndices = range(N)

# See note below on the breakdown of this command
bars = ax.bar(xIndices, dataFile.yValues, align='center')

ax.set_ylim(0, dataFile.yLim)
ax.set_ylabel(dataFile.yLabel)
 
# Create a title, in italics
#ax.set_title('',fontstyle='italic')
 
# This sets the ticks on the x axis to be exactly where we put
# the center of the bars.
ax.set_xticks(xIndices)
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

autolabel(bars, dataFile)

pdf.savefig()
pdf.close()
p.show()
