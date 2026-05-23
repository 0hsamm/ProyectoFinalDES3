package co.edu.unbosque.proyectofinal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.TreeMap;
import java.util.zip.Deflater;

@Service
public class AgoraTokenService {

    @Value("${agora.app-id}")
    private String appId;

    @Value("${agora.app-certificate}")
    private String appCertificate;

    private static final int DURACION_TOKEN_SEGUNDOS = 3600;
    private static final short VERSION = 7;
    private static final int PRIVILEGE_JOIN_CHANNEL = 1;

    public String generarToken(String canal, int uid) {
        return null;
    }
    
    public String getAppId() {
        return appId;
    }

    private byte[] construirContenidoFirma(String appId, String canal, int uid, byte[] mensaje) {
        String uidStr = uid == 0 ? "" : String.valueOf(uid);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(appId.getBytes(StandardCharsets.UTF_8));
            out.write(canal.getBytes(StandardCharsets.UTF_8));
            out.write(uidStr.getBytes(StandardCharsets.UTF_8));
            out.write(mensaje);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
       }

    private byte[] empaquetarMensaje(int salt, int ts, TreeMap<Integer, Integer> privilegios)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeInt(out, salt);
        writeInt(out, ts);
        writeShort(out, (short) privilegios.size());
        for (var entry : privilegios.entrySet()) {
            writeShort(out, entry.getKey().shortValue());
            writeInt(out, entry.getValue());
        }
        return out.toByteArray();
    }

    private byte[] construirToken(byte[] firma, byte[] mensaje) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeShort(out, (short) firma.length);
        out.write(firma);
        writeShort(out, (short) mensaje.length);
        out.write(mensaje);
        return comprimir(out.toByteArray());
    }

    private byte[] comprimir(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buf);
            out.write(buf, 0, count);
        }
        deflater.end();
        return out.toByteArray();
    }

    private void writeInt(ByteArrayOutputStream out, int value) {
        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(value);
        out.write(buf.array(), 0, 4);
    }

    private void writeShort(ByteArrayOutputStream out, short value) {
        ByteBuffer buf = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort(value);
        out.write(buf.array(), 0, 2);
    }

    private byte[] hmacSha256(byte[] clave, byte[] data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(clave, "HmacSHA256"));
        return mac.doFinal(data);
    }
}