package terzijan.homework.generator

import java.util.concurrent.ThreadLocalRandom

class TasksGenerator {
    def random = ThreadLocalRandom.current()
    def generateMass() {
        ThreadLocalRandom.current().nextInt(1, 10)
    }

    def generateAlpha() {
        [30, 45, 60].get(ThreadLocalRandom.current().nextInt(0, 3))
    }

    def generateMu() {
        ThreadLocalRandom.current().nextDouble(0.1, 0.5).round(2)
    }

    def generateAbstractForce() {
        ThreadLocalRandom.current().nextInt(5, 16)
    }

    def generateForceTr() {
        ThreadLocalRandom.current().nextDouble(1.5, 4.0).round(2)
    }
}
