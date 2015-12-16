import cv2
import numpy as np
import math
import operator
from os import listdir
from os.path import isfile, join, isdir

def ang(a, b):
    dot = a[0] * b[0] + a[1] * b[1]
    by = math.sqrt(a[0] * a[0] + a[1] * a[1]) * math.sqrt(b[0] * b[0] + b[1] * b[1])
    return dot / by

def nn(a):
    return float(a) / 100

dpath = 'test'
outFile = open('result-test', 'w')
outFile.write('')
outFile.close()
outFile = open('result-test', 'a')

cases = [d for d in listdir(dpath) if isdir(join(dpath, d))]

for ci, case in enumerate(cases):
    onlyfiles = [f for f in listdir(join(dpath, case)) if isfile(join(dpath, case, f))]

    kneg = False
    kleft = False
    aneg = False
    aleft = False

    for fi, f in enumerate(onlyfiles):
        img = cv2.imread(join(dpath, case, f), 0)
        if img is None:
            continue
        contours, hierarchy = cv2.findContours(img.copy(), 1, 2)
        largest_areas = sorted(contours, key=cv2.contourArea)
        xi,yi,wi,hi = cv2.boundingRect(largest_areas[-1])

        yh = int(yi+0.1*hi)
        yk = int(yi+0.7*hi)
        ya = int(yi+0.9*hi)

        hz = (np.flatnonzero(img[yh]))
        kz = (np.flatnonzero(img[yk]))
        az = (np.flatnonzero(img[ya]))

        if len(kz) - 1 == kz[-1] - kz[0] :
            kneg = True
        else:
            if kneg == True:
                kleft = not kleft
            kneg = False

        if len(az) - 1 == az[-1] - az[0] :
            aneg = True
        else:
            if aneg == True:
                aleft = not aleft
            aneg = False

        blank_image = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)

        # head
        hh = (int((hz[0]+hz[-1])/2),yh)
        cv2.circle(blank_image,hh,5,(255,0,255),10)
        # knee lr
        if kleft:
            lk = (kz[0]+5,yk)
            rk = (kz[-1]-5,yk)
        else:
            lk = (kz[-1]-5,yk)
            rk = (kz[0]+5,yk)
        # ankle lr
        if aleft:
            la = (az[0]+5,ya)
            ra = (az[-1]-5,ya)
        else:
            la = (az[-1]-5,ya)
            ra = (az[0]+5,ya)

        cv2.circle(blank_image,lk,2,(0,255,255),10)
        cv2.circle(blank_image,rk,2,(255,255,0),10)
        cv2.circle(blank_image,la,2,(0,255,255),10)
        cv2.circle(blank_image,ra,2,(255,255,0),10)

        llk = tuple(map(operator.sub, lk, hh))
        rrk = tuple(map(operator.sub, rk, hh))
        lla = tuple(map(operator.sub, la, hh))
        rra = tuple(map(operator.sub, ra, hh))
        lka = tuple(map(operator.sub, la, lk))
        rka = tuple(map(operator.sub, ra, rk))

        # print nn(llk[0]), nn(llk[1]), nn(rrk[0]), nn(rrk[1]), nn(lla[0]), nn(lla[1]), nn(rra[0]), nn(rra[1])
        # print 'l', ang(lka, llk)
        # print 'r', ang(rka, rrk)
        result = [nn(llk[0]), nn(llk[1]), nn(rrk[0]), nn(rrk[1]), nn(lla[0]), nn(lla[1]), nn(rra[0]), nn(rra[1]), ang(lka, llk), ang(rka, rrk), '1 \n']
        outFile.write(" ".join(map(str, result)))

        cv2.imshow('blank_image', blank_image)
        cv2.waitKey(30)
outFile.close()
cv2.destroyAllWindows()
