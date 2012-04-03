import numpy as np
import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator, FormatStrFormatter
import sys

class Graph():
    def __init__(self,title):
        self.title = title
        self.criterias = []
        self.combinationsNames = [] # eg. comb1 , comb2 .. etc
        self.ylabel = 'ylabel'

class Criteria():
    def __init__(self,name):
        self.name = name
        self.color = 'r'
        self.values = []

def drawGraph(graph):
    N = len(graph.criterias[0].values)
    ind = np.arange(N+1)                  # the x locations for the groups
    width = 1.0/len(graph.criterias)-0.02 # the width of the bars

    plt.cla()
    ax = plt.subplot(111)
    #== GRID =======
    #plt.grid(True)
    ax.yaxis.set_major_locator(MultipleLocator(20.0))
    ax.yaxis.set_minor_locator(MultipleLocator(5.0))
    ax.xaxis.grid(True,'minor')
    ax.yaxis.grid(True,'minor')
    ax.xaxis.grid(True,'major',linewidth=1.5)
    ax.yaxis.grid(True,'major',linewidth=1.5)
    ax.set_axisbelow(True)
    #==== GRID =====

    plt.ylim([0,100])
    plt.xlim([-1,N])
    rects = []
    w = 0

    for criteria in graph.criterias:
        plt.plot(criteria.values,'o-',color=criteria.color,label=criteria.name)

    plt.ylabel(graph.ylabel)
    plt.title(graph.title)
    plt.xticks(ind, tuple(graph.combinationsNames))

    trects = tuple(rects)

    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])

    # Put a legend to the right of the current axis
    ax.legend(loc='center left', bbox_to_anchor=(1, 0.5),fancybox = True,
            shadow=True,ncol=1)

    return plt

def savePlot(plot,fileName):
    plot.savefig(fileName)


def loadGraphs(fileName):
    f = open(fileName,'r')
    filestr = f.readlines()
    f.close()
    str = ""
    for i in filestr:
        str+=i
    import json
    ret = json.loads(str)
    graphs = []
    if type(ret) == type([]):
        graphs = ret
    else:
        graphs.append(ret)
    for graph in graphs:
        g = Graph(graph['title'])
        g.combinationsNames = graph['combinations_names']
        g.ylabel = graph['ylabel']
        for cr in graph['criterias']:
            criteria = Criteria(cr['name'])
            criteria.color = cr['color']
            criteria.values = cr['values']
            g.criterias.append(criteria)
        savePlot(drawGraph(g),g.title)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print 'usage : python grapher.py graph_file_path'
        sys.exit()
    else:
        loadGraphs(sys.argv[1])
