
import pylab as p
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib import rc
import matplotlib.pyplot as plt


rc("font", size=17)
pdf = PdfPages('barcharg_demo_pdf2.pdf')
fig = p.figure(figsize=(5,5))
ax = fig.add_axes((0.15, 0.25, 0.75, 0.7))
 
# note the change: I'm only supplying y data.
y = [44.2, 21.7, 1.6, 0.8]
 
# Calculate how many bars there will be
N = len(y)
 
# Generate a list of numbers, from 0 to N
# This will serve as the (arbitrary) x-axis, which
# we will then re-label manually.
ind = range(N)
 
# See note below on the breakdown of this command
bars = ax.bar(ind, y, align='center')
ax.set_ylim(0, 46)

#Create a y label
ax.set_ylabel('Network I/O (GB)')
 
# Create a title, in italics
#ax.set_title('',fontstyle='italic')
 
# This sets the ticks on the x axis to be exactly where we put
# the center of the bars.
ax.set_xticks(ind)
ax.set_yticks( range(0, 51, 5) )
 
# Labels for the ticks on the x axis.  It needs to be the same length
# as y (one label for each bar)
group_labels = ['Random', 'Domain',
                 'METIS-balanced', 'METIS-default']

# Set the x tick labels to the group_labels defined above.
ax.set_xticklabels(group_labels)
# Extremely nice function to auto-rotate the x axis labels.
# It was made for dates (hence the name) but it works
# for any long x tick labels
fig.autofmt_xdate()

def autolabel(rects):
    # attach some text labels
    for rect in rects:
        height = rect.get_height()
        plt.text(rect.get_x()+rect.get_width()/2.,
                 1.05*height, '%.1f' % height,
                 ha='center', va='bottom')

autolabel(bars)

pdf.savefig()
pdf.close()
#p.show()
