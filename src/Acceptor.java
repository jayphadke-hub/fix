import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Acceptor extends Thread {
    private FixMessageQueue inbound, outbound;
    private Socket socket;
    Acceptor(FixMessageQueue inbound, FixMessageQueue outbound)
    {
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public void sendLoop()
    {
        while(!socket.isClosed())
        {
            try 
            {
                FixMessage m=(FixMessage)outbound.take();
                byte[]bArr=m.encode().getBytes(StandardCharsets.UTF_8);
                socket.getOutputStream().write(bArr);       
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        int port = 9273;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port);
            socket = serverSocket.accept();
            Thread sThread=new Thread(this::sendLoop,"SendThread");
            sThread.start();
            InputStream is = socket.getInputStream();
            int b = 0;
            StringBuilder sb = new StringBuilder();
            while ((b = is.read()) != -1) {
                char a = (char) b;
                sb.append(a);
                if (a == '\u0001') {
                    boolean flag = sb.toString().contains("\u000110=");
                    if (flag) {
                        FixMessage m = new FixMessage();
                        m.decode(sb.toString());
                        inbound.put(m);
                        sb.setLength(0);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR :" + e.getMessage());
        }
    }
}
