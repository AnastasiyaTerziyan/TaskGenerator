package terzijan.homework.generator

import groovy.sql.Sql

class SqlUtils {
    static Sql sql

    SqlUtils() {
        sql = Sql.newInstance("jdbc:h2:~/tasksGenerator",
                'org.h2.Driver')
    }
}
