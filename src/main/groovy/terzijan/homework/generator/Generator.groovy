package terzijan.homework.generator

import groovy.sql.Sql
import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic

import java.nio.Buffer

import static java.lang.Math.*

class Generator {

    static final FtrTask = 'Брусок массой m = ${mass} кг движется поступательно по горизонтальной плоскости \n' +
            'под действием постоянной силы, направленной под углом a = ${alpha} к \n' +
            'горизонту. Модуль этой силы F = ${abstractForce} H. \n' +
            'Коэффициент трения между бруском и плоскостью u = ${mu}. \n' +
            'Чему равен модуль силы трения, действующей на брусок? Ответ приведите в ньютонах.'
    static final massTask = 'Массивный брусок движется поступательно по горизонтальной плоскости под действием \n' +
            'постоянной силы, направленной под углом a = ${alpha} к горизонту. \n' +
            'Модуль этой силы F = ${abstractForce} H Коэффициент трения между бруском и плоскостью u = ${mu} \n' +
            'Модуль силы трения, действующей на брусок равен ${ForceTr} Н. \n' +
            'Чему равна масса бруска? Ответ приведите в килограммах. \n'
    static final muTask = 'Брусок массой m = ${mass} кг движется поступательно по горизонтальной плоскости под действием \n' +
            'постоянной силы, направленной под углом a = ${alpha} к горизонту. \n' +
            'Модуль этой силы F = ${abstractForce} H Модуль силы трения, действующей на брусок равен ${ForceTr} Н. \n' +
            'Чему равен коэффициент трения между бруском и плоскостью? Ответ с точностью до первого знака после запятой. \n'
    static final abstractForceTask = 'Брусок массой m = ${mass} кг движется поступательно по горизонтальной плоскости под действием \n' +
            'постоянной силы F, направленной под углом a = ${alpha} к горизонту. \n' +
            'Коэффициент трения между бруском и плоскостью u = ${mu} \n' +
            'Модуль силы трения, действующей на брусок равен ${ForceTr} Н. Чему равен модуль силы F? Ответ приведите в Н.\n'
    static final String HOW_MUCH = 'Укажите количество заданий для генерации'
    static TasksGenerator generator = new TasksGenerator()
    static templateEngine = new SimpleTemplateEngine()
    static Solver solver = new Solver()




    static generateTask(BufferedReader reader, Unknown taskType, Sql sql) {
        println(HOW_MUCH)
        def n = reader.readLine().toInteger()
        for (int i = 0; i < n; i++) {
            generateTask(taskType, sql)
        }
        println("Задания готовы. Введите любую команду для возврата в главное меню")
    }

    static generateTask(Unknown taskType, Sql sql) {
        switch (taskType) {
            case Unknown.Ftr:
                def bindings = [mass: generator.generateMass(), alpha: generator.generateAlpha(),
                                abstractForce: generator.generateAbstractForce(),
                                mu: generator.generateMu()]
                def template =  templateEngine.createTemplate(FtrTask).make(bindings)
                def result = solver.solve(taskType, bindings.mu, solver.countN(bindings.mass, bindings.abstractForce, bindings.alpha)).round(2)
                saveToDb(sql, template, result, taskType)
                break
            case Unknown.MASS:
                def bindings = [alpha: generator.generateAlpha(), abstractForce: generator.generateAbstractForce(),
                                mu: generator.generateMu(),
                                ForceTr: generator.generateForceTr()]
                def template =  templateEngine.createTemplate(massTask).make(bindings)
                def result = solver.solve(taskType, bindings.ForceTr, bindings.mu, bindings.abstractForce, bindings.alpha).round(2)
                saveToDb(sql, template, result, taskType)
                break
            case Unknown.MU:
                def bindings = [mass: generator.generateMass(), alpha: generator.generateAlpha(),
                                abstractForce: generator.generateAbstractForce(), ForceTr: generator.generateForceTr()]
                def template =  templateEngine.createTemplate(muTask).make(bindings)
                def result = solver.solve(taskType, bindings.ForceTr, solver.countN(bindings.mass, bindings.abstractForce,
                        bindings.alpha)).round(2)
                saveToDb(sql, template, result, taskType)
                break
            case Unknown.Fabst:
                def bindings = [mass: generator.generateMass(), alpha: generator.generateAlpha(), mu: generator.generateMu(),
                                ForceTr: generator.generateForceTr()]
                def template =  templateEngine.createTemplate(abstractForceTask).make(bindings)
                def result = solver.solve(taskType, bindings.mass, bindings.ForceTr, bindings.mu, bindings.alpha).round(2)
                saveToDb(sql, template, result, taskType)
                break
            default:
                println('Такого типа заданий нет, возврат в главное меню')
        }
    }

    private static void saveToDb(Sql sql, template, result, Unknown type) {
        sql.executeInsert("INSERT INTO TASKS (TASK_FULL_TEXT, TASK_SOLUTION, UNKNOWN) VALUES (${template.toString()}," +
                " ${result}, ${type.toString()})")
    }

    static showTask(BufferedReader reader, Unknown type, Sql sql) {
        sql.eachRow(type ? "select * from TASKS where UNKNOWN = ${type.toString()}" : "select * from TASKS") { row ->
            println("Задание ${row.ID}")
            println(row.TASK_FULL_TEXT)
            println('_________________________________________\n')
        }
        println("Введите ответ на задание в формате [№:ответ], без пробела, ответ введите с точностью до 2 знаков," +
                "чтобы выйти в главное меню введите stop")
        def answer = reader.readLine()
        while (answer != 'stop') {
            if (answer) {
                answer = answer.tokenize(':')
                def correctAnswer = sql.rows("select TASK_SOLUTION from TASKS where ID = ${answer[0].toLong()}")[0][0]
                if (correctAnswer.toString() == answer[1]) {
                    println("Ответ верен.")
                } else {
                    println("Ответ неверен.")
                }
            }
            answer = reader.readLine()
        }
    }

    static void main(String... args) {
        def sql = new SqlUtils().sql
        def reader = System.in.newReader()

        println("Введите что-то чтобы начать.")
        while (reader.readLine() != '\\q') {
            println("I - генерация заданий \n" +
                    "II - ответы на задания")
            def line = reader.readLine()
            if (line == 'I') {
                println("Какого типа задания генерировать? \n" +
                        "1 - С неизвестной силой трения \n" +
                        "2 - С неизвестной массой \n" +
                        "3 - С неизвестным коэффициентом трения \n" +
                        "4 - С неизвестной силой воздействия")
                switch (reader.readLine()) {
                    case '1':
                        generateTask(reader, Unknown.Ftr, sql)
                        continue
                        break
                    case '2':
                        generateTask(reader, Unknown.MASS, sql)
                        continue
                        break
                    case '3':
                        generateTask(reader, Unknown.MU, sql)
                        continue
                        break
                    case '4':
                        generateTask(reader, Unknown.Fabst, sql)
                        continue
                        break
                    default:
                        println('Такого типа заданий нет, возврат в главное меню')
                        continue
                }
            } else if (line == 'II') {
                println("Какого типа задания показать? \n" +
                        "1 - С неизвестной силой трения \n" +
                        "2 - С неизвестной массой \n" +
                        "3 - С неизвестным коэффициентом трения \n" +
                        "4 - С неизвестной силой воздействия\n" +
                        "5 - Все задания")
                switch (reader.readLine()) {
                    case '1':
                        showTask(reader, Unknown.Ftr, sql)
                        continue
                        break
                    case '2':
                        showTask(reader, Unknown.MASS, sql)
                        continue
                        break
                    case '3':
                        showTask(reader, Unknown.MU, sql)
                        continue
                        break
                    case '4':
                        showTask(reader, Unknown.Ftr, sql)
                        continue
                        break
                    case '5':
                        showTask(reader, null, sql)
                        continue
                        break
                    default:
                        println('Такого типа заданий нет, возврат в главное меню')
                        continue
                }
            }
        }
    }
}
