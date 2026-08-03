
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Handler extends Thread {
    private Map<Integer,FixMessage> store;

    private FixMessageQueue inbound, outbound;
    private int expected=1;
    private int outgoing=1;
    private LocalDateTime lastSent;
    
    Handler(FixMessageQueue inbound, FixMessageQueue outbound)
    {
        this.inbound = inbound;
        this.outbound = outbound;
        this.store = new LinkedHashMap<>();
        this.lastSent = LocalDateTime.now();
    }

    private void send(FixMessage m)
    {
        try {
            m.setField(34, String.valueOf(outgoing));
            if(!"Y".equals(m.getField(43)))
            {
                store.put(outgoing++,m);
            }
            lastSent=LocalDateTime.now();
            outbound.put(m);
            System.out.println("added to outbound "+m.toString());
        } catch (Exception e) {
            System.out.println("ERROR WHILE ADDING MSG TO OUTBOUND Q");
        }
    }

    private FixMessage getMessage(String msgType)
    {
        FixMessage m = new FixMessage();
        m.setField(8, "FIX.4.4");
        m.setField(35, msgType);
        m.setField(49, "BABA");
        m.setField(56, "JAY");
        m.setField(34, String.valueOf(outgoing));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
        String sendingTime = now.format(formatter);
        m.setField(52, sendingTime);
        return m;
    }

    @Override
    public void run(){
        try {
            while (true) {
                LocalDateTime now=LocalDateTime.now();
                Duration d=Duration.between(lastSent, now);
                if(d.toSeconds()>30)
                {
                    FixMessage hb=getMessage("0");
                    send(hb);
                }
                FixMessage msg=(FixMessage)inbound.poll(10,TimeUnit.SECONDS);
                if (msg==null) continue;
                System.out.println("Process handling "+msg.toString());
                int seq=msg.getInt(34);
                if(seq<expected && !"Y".equals(msg.getField(43))) throw new Exception("FATAL ERROR: RECD SEQ NO < EXPECTED");
                else if(seq>expected)
                {
                    FixMessage m = getMessage("2");
                    m.setField(7,String.valueOf(expected));
                    m.setField(16,String.valueOf(seq-1));
                    send(m);
                }
                if(msg.getField(35).equals("A"))
                {
                    FixMessage logon = getMessage("A"); 
                    logon.setField(98,"0");
                    logon.setField(108,"30");
                    send(logon);
                }
                else if(msg.getField(35).equals("1"))
                {
                    FixMessage hb=getMessage("0");
                    send(hb);
                }
                else if(msg.getField(35).equals("2"))
                {
                    int beginSeqNo=msg.getInt(7);
                    int endSeqNo=msg.getInt(16);
                    if(endSeqNo==0)
                    {
                        endSeqNo=outgoing-1;
                    }
                    for(int i=beginSeqNo;i<=endSeqNo;i++)
                    {
                        FixMessage m=store.get(i);
                        m.setField(43,"Y");
                        send(m);
                    }
                }
                else if(msg.getField(35).equals("4"))
                {
                    expected=msg.getInt(36)-1;
                }
                else if(msg.getField(35).equals("5"))
                {
                    FixMessage logout=getMessage("5");
                    send(logout);
                }
                expected++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
