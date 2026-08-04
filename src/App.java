public class App {
    public static void main(String[] args) throws Exception {
        Client cli=new Client();
        Acceptor acc=new Acceptor();
        acc.start();
        Thread.sleep(5000);
        cli.start();
    }
}
