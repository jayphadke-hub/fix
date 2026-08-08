import java.io.FileOutputStream;
import java.io.*;
public class SessionConfig {
    private String fixVersion;
    private SessionRole role;
    private String senderCompID;
    private String targetCompID;
    private int hbInterval;
    private String ip;
    private int port;
    SessionConfig(String fixVersion,SessionRole role, String senderCompID, String targetCompID, int hbInterval, String ip, int port) {
        this.role = role;
        this.senderCompID = senderCompID;
        this.targetCompID = targetCompID;
        this.hbInterval = hbInterval;
        this.ip = ip;
        this.port = port;
        this.fixVersion=fixVersion;
    }

    public SessionRole getRole() {
        return role;
    }

    public void setRole(SessionRole role) {
        this.role = role;
    }

    public String getSenderCompID() {
        return senderCompID;
    }

    public void setSenderCompID(String senderCompID) {
        this.senderCompID = senderCompID;
    }

    public String getTargetCompID() {
        return targetCompID;
    }

    public void setTargetCompID(String targetCompID) {
        this.targetCompID = targetCompID;
    }

    public int getHbInterval() {
        return hbInterval;
    }

    public void setHbInterval(int hbInterval) {
        this.hbInterval = hbInterval;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public void save(String fileName)throws Exception
    {
        File f=new File(fileName);
        FileOutputStream fos=new FileOutputStream(f);
        StringBuilder sb=new StringBuilder();
        sb.append("IP="+ip);
        sb.append("\n");
        sb.append("sender="+senderCompID);
        sb.append("\n");
        fos.write(sb.toString().getBytes());
    }
    public static void main(String[]args) throws Exception
    {
        SessionConfig c=new SessionConfig("FIX.4.4",SessionRole.INITIATOR,"JAY","BABA",30,"127.0.0.1",9273);
        c.save("a.txt");
    }
}
