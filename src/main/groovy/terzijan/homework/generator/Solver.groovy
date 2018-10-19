package terzijan.homework.generator

import static java.lang.Math.sin
import static java.lang.Math.toRadians

class Solver {
    static final G = 9.8

    double countMass(def Ftr, def mu, def F, def a) {
        (Ftr/mu + F*sin(toRadians(a))) / G
    }

    double countFTr(def mu, def N) {
        mu * N
    }

    double countN(def mass, def F, def a) {
        mass * G - F * sin(toRadians(a))
    }

    double countNf(def Ftr, def mu) {
        Ftr / mu
    }

    double countF(def mass, def Ftr, def mu, def a) {
        (mass * G - (Ftr / mu)) / sin(toRadians(a))
    }

    double solve(Unknown unknown, Object... additionalArgs) {
        switch (unknown) {
            case Unknown.Ftr:
                countFTr(additionalArgs[0], additionalArgs[1])
                break
            case Unknown.MASS:
                countMass(additionalArgs[0], additionalArgs[1], additionalArgs[2], additionalArgs[3])
                break
            case Unknown.MU:
                countMu(additionalArgs[0], additionalArgs[1])
                break
            case Unknown.Fabst:
                countF(additionalArgs[0], additionalArgs[1], additionalArgs[2], additionalArgs[3])
                break
        }
    }
}
