package spock4.mockingandstubbing

import spock.lang.Specification



class MockingSpeck extends Specification {

    interface Subscriber {
        void receive(String message)
    }

    class Publisher {
        final subscribers = new LinkedList<Subscriber>();

        void add(subscriber) {
            this.subscribers.add(subscriber)
        }

        void send(String message) {
            this.subscribers.each { it.receive message }
        }
    }

    Publisher publisher = new Publisher()
    final subscriber = Mock(Subscriber)
    Subscriber subscriber2 = Mock()

    def "should send messages to all subscribers"() {
        setup:
        publisher.add(subscriber)
        publisher.add(subscriber2)

        when:
        publisher.send("hello")

        then:
        1 * subscriber.receive("hello")
        1 * subscriber2.receive("hello")
        // Can be replaced with
        // 2 * _.receive("hello")
    }

    /* Other cardinality options:
    1 * subscriber.receive("hello")         // exactly one call
    0 * subscriber.receive("hello")         // zero calls
    (1..3) * subscriber.receive("hello")    // between one and three calls (inclusive)
    (1.._) * subscriber.receive("hello")    // at least one call
    (_..3) * subscriber.receive("hello")    // at most three calls
    _ * subscriber.receive("hello")         // any number of calls, including zero (is it ever needed?; no 'Strict Mocking')
    1 * subscriber./r.*e/("hello")          // a method whose name matches the given regular expression
                                            // (here: method name starts with 'r' and ends in 'e')
     */

    def "decorating publisher should contain original string in output"() {
        setup:
        final decoratingPublisher = new Publisher() {
            @Override
            void send(String message) {
                super.send("*** $message ***")
            }
        }
        decoratingPublisher.add(subscriber)

        when:
        decoratingPublisher.send("hello")

        then:
        1 * subscriber.receive({ it.contains("hello") })
    }

    /* Other useful and practical ways of matching parameters:
    1 * subscriber.receive("hello")     // an argument that is equal to the String "hello"
    1 * subscriber.receive(!"hello")    // an argument that is unequal to the String "hello"
    1 * subscriber.receive()            // the empty argument list (would never match in our example)
    1 * subscriber.receive(_)           // any single argument (including null)
    1 * subscriber.receive(*_)          // any argument list (including the empty argument list)
    1 * subscriber.receive(!null)       // any non-null argument
    1 * subscriber.receive(_ as String) // any non-null argument that is-a String
    1 * subscriber.receive({ it.size() > 3 }) // an argument that satisfies the given predicate
                                              // (here: message length is greater than 3)
     */

    def "spock should allow strict mocking"() {
        setup:
        final List<String> auditLog = Mock()
        final auditingPublisher = new Publisher() {
            @Override
            void send(String message) {
                auditLog.add("Sending: $message")
                super.send(message)
                auditLog.add("Sent: $message")
            }
        }
        auditingPublisher.add(subscriber)

        when:
        auditingPublisher.send("hello")

        then:
        1 * subscriber.receive("hello") // demand one 'receive' call on 'subscriber'
        _ * auditLog._                  // allow any interaction with 'auditLog'
        0 * _                           // don't allow any other interaction

    }

    def "spock should allow declaring invocation count at the beginning"() {
        given:
        final subscriber = Mock(Subscriber) {
            1 * receive(_)
        }
        publisher.add(subscriber)

        when:
        publisher.send("hello")

        then:
        true
    }
}

class StubbingSpec extends Specification {

    interface Subscriber {
        String receive(String message)
    }

    class Publisher {
        final subscribers = new LinkedList<Subscriber>();

        void add(subscriber) {
            this.subscribers.add(subscriber)
        }

        boolean send(String message) {
            Set<String> responses = new HashSet<>()
            this.subscribers.each { it -> responses << it.receive(message) }
            responses.remove("ok")
            return responses.empty
        }
    }

    final Subscriber subscriber = Stub()
    final Publisher publisher = new Publisher()

    def "Publisher should return true if all subscribers succeeded"() {
        given:
        subscriber.receive(_) >> "ok"
        publisher.add(subscriber)

        when:
        final hasResponseSucceeded = publisher.send("Hello")

        then:
        hasResponseSucceeded
    }

    def "Publisher should return false when subscriber failed"() {
        given:
        subscriber.receive("Goodbye") >> "ok"
        subscriber.receive("Hello") >> "fail"
        publisher.add(subscriber)

        when:
        final hasResponseSucceeded = publisher.send("Hello")

        then:
        !hasResponseSucceeded
    }

    def "Publisher should return false if at least one subscriber failed"() {
        given:
        subscriber.receive(_) >>> ["ok", "fail"]
        publisher.add(subscriber)
        publisher.add(subscriber)

        when:
        final hasResponseSucceeded = publisher.send("Hello")

        then:
        !hasResponseSucceeded
    }

    def "Subscriber should send not ok on certain parameters"() {
        given:
        subscriber.receive(_) >> { args -> args[0] == "Hello" ? "ok" : "fail" }
        // or
        // subscriber.receive(_) >> { String message -> message == "Hello" ? "ok" : "fail" }
        publisher.add(subscriber)

        when:
        final hasResponseSucceeded = publisher.send("Hi")

        then:
        !hasResponseSucceeded
    }

    def "Publisher should call subscriber once per addition"() {
        given:
        final Subscriber subscriber = Mock()
        1 * subscriber.receive(_) >> "ok"
        publisher.add(subscriber)

        when:
        final hasResponseSucceeded = publisher.send("Hi")

        then:
        hasResponseSucceeded
    }

    /* Other interesting things:
    Spy
    PoolingConditions
    old()
     */
}
