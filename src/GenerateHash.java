import org.apache.commons.codec.digest.DigestUtils;

public class GenerateHash {

    public static void main(String[] args) {
        System.out.println(DigestUtils.sha256Hex("guest"));
    }
}
