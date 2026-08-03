import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Client extends Thread{
    int outgoing=1;
    private FixMessage getMessage(String msgType)
    {
        FixMessage m = new FixMessage();
        m.setField(8, "FIX.4.4");
        m.setField(35, msgType);
        m.setField(49, "JAY");
        m.setField(56, "BABA");
        m.setField(34, String.valueOf(outgoing));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
        String sendingTime = now.format(formatter);
        m.setField(52, sendingTime);
        return m;
    }
public void run() {
    try{
        Socket socket=new Socket("127.0.0.1",9274);
        FixMessage m=getMessage("A");
        String msg=m.encode();
        byte[] b=msg.getBytes();
        OutputStream os=socket.getOutputStream();
        os.write(b);
        os.flush();
    }catch(Exception e)
    {
        e.printStackTrace();
    }

}
}
