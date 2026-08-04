import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
public class FixSession {
    private Socket socket;
    private FixMessageQueue inbound, outbound;
    private Handler handler;
    private SessionRole role;
    //private SessionState state;
    public FixSession(Socket socket,SessionRole role) {
        this.role=role;
        //this.state=SessionState.NEW;
        this.socket = socket;
        inbound = new FixMessageQueue();
        outbound = new FixMessageQueue();
        this.handler = new Handler(inbound, outbound, role);
        handler.start();
        Thread sThread=new Thread(this::sendLoop,"SendThread");
        sThread.start();
        sThread=new Thread(this::receiveLoop,"ReceiveThread");
        sThread.start();
    }
    public void sendLoop()
    {
        while(!socket.isClosed())
        {
            try 
            {
                FixMessage m=(FixMessage)outbound.take();
                byte[]bArr=m.encode().getBytes();
                socket.getOutputStream().write(bArr);
                System.out.println("sent msg on wire"+m.toString());       
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }

    public void receiveLoop() {
        try {
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
                        System.out.println("recieved raw bytes " +m.toString());
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
