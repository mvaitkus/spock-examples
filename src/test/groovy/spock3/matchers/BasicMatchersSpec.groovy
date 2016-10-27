package spock3.matchers

import spock.lang.Specification
import static spock.util.matcher.HamcrestSupport.*


class BasicMatchersSpec extends Specification {

    final emptyStack = new Stack()

    // Uncomment for tests to fail
//    def setup() {
//        emptyStack.push("Haha")
//    }

    def "empty stack should tell that it's empty"() {
        expect:
        emptyStack.empty
    }

    def "empty stack should not report empty as false"() {
        expect:
        emptyStack.empty != false
    }

    def "empty stack size should be less than 1"() {
        expect:
        emptyStack.size() < 1
    }

    def "empty stack should behave as empty stack"() {
        expect:
        stackIsEmpty(emptyStack)
    }

    def stackIsEmpty(stack) {
        stack.empty && stack.size() == 0
    }

    def "report more clearly that empty stack should behave as empty stack"() {
        expect:
        stackIsClearlyEmpty(emptyStack)
    }

    void stackIsClearlyEmpty(stack) {
        assert stack.empty
        assert stack.size() == 0
    }

    def "check that stack is really empty in place"() {
        expect:
        with(emptyStack) {
            empty
            size() == 0
        }
    }

    def "use some english to check that stack is empty"() {
        expect:
        that emptyStack.size(), is(equalTo(0))
    }

}

import static org.hamcrest.Matchers.*

class ExtendedHamcrestMatchersSpec extends Specification {
    def "check if map has a key with hamcrest" () {
        given:
        final sampleMap = [name: 'mrhaki']

        expect:
        sampleMap hasKey('name')
    }

    def "check that map has key with hamcrest"() {
        given:
        final sampleMap = [name: 'mrhaki']

        expect:
        that sampleMap, hasKey('name')
    }

    def "use expect to check that map has a key with hamcrest"() {
        when:
        final sampleMap = [name: 'mrhaki']

        then:
        expect sampleMap, hasKey('name')
    }
}
