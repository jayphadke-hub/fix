import java.util.*;

public class FixMessage {
    public static final String SOH = "\u0001";
    private Map<Integer, String> fields;

    public FixMessage() {
        fields = new LinkedHashMap<>();
    }

    public void setField(int tag, String val) {
        fields.put(tag, val);
    }

    public String getField(int tag) {
        return fields.get(tag);
    }

    public int getInt(int tag)
    {
        return Integer.parseInt(getField(tag));
    }
    
    public String encode() {
        StringBuilder sb = new StringBuilder();
        fields.forEach((tag, val) -> {
            if (tag != 9 && tag != 10) {
                sb.append(tag);
                sb.append("=");
                sb.append(val);
                sb.append(SOH);
            }
        });
        int fsoh = sb.indexOf(SOH);
        int bodylen = sb.length() - fsoh - 1;
        sb.insert(fsoh + 1, "9=" + bodylen + SOH);
        int sum = 0;
        for (int i = 0; i < sb.length(); i++) {
            sum += sb.charAt(i);
        }
        String padded = String.format("%03d", sum % 256);
        sb.append("10=").append(padded).append(SOH);
        return sb.toString();
    }

    @Override
    public String toString() {
        return encode().replace(SOH, "|");
    }

    public void decode(String msg) {
        String[] flds = msg.split(SOH);
        fields.clear();
        for (String f : flds) {
            if (f == null || f.isEmpty()) continue;
            int i = f.indexOf('=');
            if (i <= 0) continue;
            int tag = Integer.parseInt(f.substring(0, i));
            String val = f.substring(i + 1);
            fields.put(tag, val);
        }
    }

    public static boolean isValidCheckSum(String msg) {
        int sum = 0;
        int index = msg.indexOf(SOH + "10=") + 1;
        if (index == 0) return false;
        for (int i = 0; i < index; i++) {
            sum += msg.charAt(i);
        }
        sum %= 256;
        try {
            return sum == Integer.parseInt(msg.substring(index + 3, index + 6));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidBodyLength(String msg) {
        int firstsoh = msg.indexOf(SOH);
        int secondsoh = msg.indexOf(SOH, firstsoh + 1);
        int secondlastsoh = msg.indexOf(SOH + "10=");
        int len = secondlastsoh - secondsoh;
        return len == Integer.parseInt(msg.substring(firstsoh + 3, secondsoh));
    }
}
