package diff;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

class BinaryService {

    static String byteArrayToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    static String stringToBase64(String content) throws UnsupportedEncodingException {
        return byteArrayToBase64(content.getBytes(StandardCharsets.UTF_8));
    }

    static String base64ToString(byte[] bytes) {
        return new String(Base64.getDecoder().decode(bytes), StandardCharsets.UTF_8);
    }

    static String fileToBase64(String contentType, Path path) throws IOException {
        return contentType + ";base64," + byteArrayToBase64(Files.readAllBytes(path));
    }

    static String base64ToFile(String encoded) throws IOException {
        Binary binary = extractBinaryFromEncoded(encoded);
        String randomFileName = "/tmp/" + UUID.randomUUID().toString() + "." + binary.getSuffix();

        // TODO (vasi) (improvement): use Files.createTempFile().deleteOnExit()
        FileOutputStream fos = new FileOutputStream(new File(randomFileName));
        fos.write(Base64.getDecoder().decode(binary.getBase64()));
        fos.close();

        return randomFileName;
    }

    static Binary extractBinaryFromEncoded(String encoded) throws StringIndexOutOfBoundsException {
        String contentType = encoded.substring(encoded.indexOf(':')+1, encoded.indexOf('/'));
        String suffix = encoded.substring(encoded.indexOf('/')+1, encoded.indexOf(';'));
        String base64 = encoded.substring(encoded.indexOf(',')+1, encoded.length());

        return new Binary(contentType, suffix, base64);
    }

    static String md5Sum(Path path) throws NoSuchAlgorithmException, IOException {
        byte[] bytes = Files.readAllBytes(path);
        byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
        return DatatypeConverter.printHexBinary(hash);
    }

    static boolean isBase64(String encoded) {
        try{
            Binary binary = extractBinaryFromEncoded(encoded);

            if (binary.getBase64().isEmpty() || binary.getBase64().length() % 4 != 0)
                return false;

            Base64.Decoder decoder = Base64.getDecoder();

            decoder.decode(binary.getBase64());
        } catch (StringIndexOutOfBoundsException | IllegalArgumentException e) {
            // invalid base64 received
            return false;
        }
        return true;
    }
}
