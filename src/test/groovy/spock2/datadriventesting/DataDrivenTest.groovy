package spock2.datadriventesting

import groovy.sql.Sql
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class NotConvenientMathSpec extends Specification {
    def "Should return maximum of two numbers"() {
        expect:
        // exercise math method for a few different inputs
        Math.max(1, 3) == 3
        Math.max(7, 4) == 7
        Math.max(0, 0) == 0
    }
}

class LetsUseTableForMathSpec extends Specification {
    def "Should return maximum of two numbers"(int a, int b, int c) {
        expect:
        Math.max(a, b) == c

        where:
        a | b | c
        1 | 3 | 3
        7 | 4 | 7
        0 | 0 | 0
    }
}

class ButIDontWantToDefineParametersSpec extends Specification {
    def "Should return maximum of two numbers"() {
        // the parameters are supported for better ide support, no longer needed for Intellij
        expect:
        Math.max(a, b) == c

        where:
        a | b || c
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }
}

class IWantToKnowWhichIterationFailedSpec extends Specification {

    @Unroll
    def "Should return maximum of two numbers"() {
        expect:
        Math.max(a, b) == c

        where:
        a | b || c
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }
}

class IWantToKnowWhichParametersFailedSpec extends Specification {

    @Unroll
    def "maximum of numbers #a and #b is #c"() {
        expect:
        Math.max(a, b) == c

        where:
        a | b || c
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }
}

class IWantToHaveDescriptiveMethodToo extends Specification {

    @Unroll("maximum of numbers #a and #b is #c")
    def "Should return maximum of two numbers"() {
        expect:
        Math.max(a, b) == c

        where:
        a | b || c
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }
}

class WhereWithPipesSpec extends Specification {

    @Unroll("#a is less than #b")
    def "a should be less than b"() {
        expect:
        a < b

        where:
        a << [0, 1, 2]
        b << [1, 2, 3]
    }
}

class PipesCanReturnMultipleValues extends Specification {
    @Shared
    def sql = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

    def setupSpec() {
        sql.call("create table comparisons(a int, b int);")
        sql.executeInsert("insert into comparisons values(1,2);")
        sql.executeInsert("insert into comparisons values(2,3);")
    }

    @Unroll("#a is less than #b")
    def "a should be less then b"() {
        expect:
        a < b

        where:
        [a, b] << sql.rows("select * from comparisons;")
    }

    def cleanupSpec() {
        sql.close()
    }
}
