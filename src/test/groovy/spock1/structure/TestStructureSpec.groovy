package spock1.structure

import spock.lang.Specification
import spock.lang.Stepwise

class SimpleTestStructureSpec extends Specification {

    def "Stack size should be 1 after 1 element is added"() {
        given:
        final stack = new Stack<String>()

        when:
        stack.push("Element")

        then:
        stack.size() == 1
    }

    def "New stack should be empty"() {
        given:
        final stack = new Stack()

        expect:
        stack.size() == 0
    }

    def "New stack should be empty in another way"() {
        expect:
        new Stack().size() == 0
    }


}

class ExtendedStructureSpec extends Specification {

    def aGlobalStack = new Stack<String>();

    def "Stack size should be 2 when 2 elements are added"() {
        setup: "add first element to a stack"
        aGlobalStack.push("One element")

        and: "add second element to stack"
        aGlobalStack.push("Another element")

        expect: "stack is now of size 2"
        aGlobalStack.size() == 2

        cleanup: "stack to it's previous state"
        aGlobalStack.clear()
    }
}

@Stepwise
class SetupMethodsStructureSpec extends Specification {

    static Stack<Integer> aGlobalStack

    def setupSpec() {
        aGlobalStack = new Stack<Integer>()
    }

    def setup() {
        aGlobalStack.push(1)
    }

    def "Stack should show last element on peek"() {
        when:
        aGlobalStack.push(2)

        then:
        aGlobalStack.peek() == 2
    }

    def "Should not give you element from different test case"() {
        expect:
        aGlobalStack.size() == 1
    }

    def cleanup() {
        aGlobalStack.clear()
    }

    def cleanupSpec() {
        aGlobalStack = null;
        System.gc() // Collect garbage!
    }
}


class ExceptionHandlingSpec extends Specification {

    def "Empty stack should throw an exception on pop"() {
        given:
        final stack = new Stack()

        when:
        stack.pop()

        then:
        thrown(EmptyStackException)
    }

    def "Exception thrown on empty stack pop should have no cause"() {
        given:
        final stack = new Stack()

        when:
        stack.pop()

        then:
        def e = thrown(EmptyStackException)
        e.cause == null
    }

    def "Exception can be caught in another way"() {
        given:
        final stack = new Stack()

        when:
        stack.pop()

        then:
        EmptyStackException e = thrown()
        e.cause == null
    }

    def "Exception should not be thrown when stack is not empty"() {
        given:
        final stack = new Stack()
        stack.push(1)

        when:
        stack.pop()

        then:
        notThrown(EmptyStackException)
    }
}
