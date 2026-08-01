import java.util.concurrent.LinkedBlockingQueue;

// A custom type alias/typedef for BlockingQueue<FixMessage>
public class FixMessageQueue extends LinkedBlockingQueue<FixMessage> {
    public FixMessageQueue() {
        super();
    }

    public FixMessageQueue(int capacity) {
        super(capacity);
    }
}
