rr = [
    [ [1,2],[5,3],[7,4],[15,5],[18,6],[21,1],[29,2],[32,3],[35,4],[42,5],[45,6],[48,1],[50,2] ],
    [ [2,3],[5,4],[12,5],[15,6],[18,1],[25,2],[28,3],[31,4],[38,5],[41,6],[43,1],[51,2],[54,3],[56,4],[59,5] ],
    [ [1,3],[4,4],[11,5],[14,6],[17,1],[23,2],[26,3],[29,4],[35,5],[38,6],[41,1],[48,2],[50,3],[54,4],[59,5],[62,6] ],
    [ [5,2],[8,3],[11,4],[17,5],[20,6],[23,1],[30,2],[33,3],[35,4],[42,5],[45,6],[47,1],[54,2],[57,3],[58,4] ],
    [ [1,3],[3,4],[12,5],[14,6],[17,1],[24,2],[27,3],[30,4],[36,5] ],
    [ [0,1],[9,2],[13,3],[15,4],[22,5],[25,6],[28,1],[36,2],[39,3],[42,4],[50,5],[53,6],[56,1],[62,2],[65,3] ]
]
outFile = open('result-processed', 'w')
outFile.write('')
outFile.close()
outFile = open('result-processed', 'a')
with open('result') as f:
    for line in f:
        params = line.split(' ')
        case = int(params[-2])
        fram = int(params[-3])

        for rd in rr[case]:
            if fram <= rd[0]:
                print case, fram, rd[1]
                outline = " ".join(params[0:-3]) + " " + str(rd[1] - 1) + '\n'
                outFile.write(outline)
                break

        # print case, fram