package protocol;
import client.ClientProtocol;

public class Pop3Client implements ClientProtocol {
    @Override
    public byte[] processData(byte[] data) {
        System.out.println("Processing" + data.toString());
        return data;
    }
}
