import networkx as nx
from networkx.algorithms import community

G = nx.Graph()

for line in open('../../FETA3/graduation_project/result_networks/stackoverflow6Result.txt'):
    strlist = line.split()
    # if strlist[0] == 'NODE-A':
    #     n1 = 0
    # elif strlist[0] == 'NODE-B':
    #     n1 = 1
    # elif strlist[0] == 'NODE-C':
    #     n1 = 2
    # elif strlist[0] == 'NODE-D':
    #     n1 = 3
    # elif strlist[0] == 'NODE-E':
    #     n1 = 4
    # elif strlist[0] == 'NODE-F':
    #     n1 = 5
    strNodeNo0 = strlist[0].split('--')
    if strNodeNo0[0] == 'NODE':
        n1 = int(strNodeNo0[2])
    else:
        n1 = int(strNodeNo0[0])


    # if strlist[1] == 'NODE-A':
    #     n2 = 0
    # elif strlist[1] == 'NODE-B':
    #     n2 = 1
    # elif strlist[1] == 'NODE-C':
    #     n2 = 2
    # elif strlist[1] == 'NODE-D':
    #     n2 = 3
    # elif strlist[1] == 'NODE-E':
    #     n2 = 4
    # elif strlist[1] == 'NODE-F':
    #     n2 = 5
    strNodeNo1 = strlist[1].split('--')
    if strNodeNo1[0] == 'NODE':
        n2 = int(strNodeNo1[2])
    else:
        n2 = int(strNodeNo1[0])

    # G.add_edge(n1, n2, weight=strlist[2])
    # G.add_edge(n1, n2, timestamp=strlist[3])

    G.add_edge(n1, n2, timestamp=strlist[2])


def label_propagation_community(graph):
    communities_generator = list(community.label_propagation_communities(graph))
    m = []
    num = 0
    for i in communities_generator:
        num = num + len(list(i))
        print(len(list(i)))
        # print(list(i))
        m.append(list(i))
    return m, num


g, n= label_propagation_community(G)
print("============")
print(n)
print(len(g))