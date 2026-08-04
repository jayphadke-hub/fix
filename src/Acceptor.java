import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Acceptor extends Thread {
    private Socket socket;
    @Override
    public void run() {
        int port = 9274;
        try {
            ServerSocket serverSocket = new ServerSocket(port,50,InetAddress.getLoopbackAddress());
            System.out.println("Listening on port " + port);
            socket = serverSocket.accept();
            System.out.println("Connected to: "+socket.getInetAddress());
            FixSession session = new FixSession(socket, SessionRole.ACCEPTOR);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR :" + e.getMessage());
        }
    }
}
