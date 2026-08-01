public class App {
    public static void main(String[] args) throws Exception {
        FixMessageQueue inbound = new FixMessageQueue();
        FixMessageQueue outbound = new FixMessageQueue();
        Acceptor acc = new Acceptor(inbound, outbound);
        acc.start();
        Handler hndl = new Handler(inbound, outbound);
        hndl.start();
    }
}
