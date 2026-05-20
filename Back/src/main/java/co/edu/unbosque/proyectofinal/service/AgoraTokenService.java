package co.edu.unbosque.proyectofinal.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Genera tokens RTC de Agora (versión 006).
 * Documentación oficial: https://docs.agora.io/en/video-calling/get-started/authentication-workflow
 *
 * ¡IMPORTANTE! Debes configurar en application.properties:
 *   agora.app-id=TU_APP_ID
 *   agora.app-certificate=TU_APP_CERTIFICATE
 */
@Service
public class AgoraTokenService {

	@Value("${agora.app-id}")
	private String appId;

	@Value("${agora.app-certificate}")
	private String appCertificate;

	private static final int DURACION_TOKEN_SEGUNDOS = 3600;

	// Privilegio para unirse a un canal de voz/video
	private static final int PRIVILEGIO_UNIRSE_CANAL = 1;

	/**
	 * Genera un token de Agora para que el cliente pueda unirse al canal.
	 *
	 * @param canal  Nombre del canal en Agora
	 * @param uid    UID numérico del usuario (puede ser 0 para generar uno automático)
	 * @return token firmado válido por 1 hora
	 */
	public String generarToken(String canal, int uid) {

		try {
			int expiracion = (int) (System.currentTimeMillis() / 1000)
					+ DURACION_TOKEN_SEGUNDOS;

			byte[] msg = buildMessage(canal, uid, expiracion);

			byte[] firma = hmacSha256(appCertificate.getBytes(StandardCharsets.UTF_8), msg);

			return buildToken(firma, canal, uid, expiracion);

		} catch (Exception e) {
			throw new RuntimeException("Error generando token de Agora: " + e.getMessage(), e);
		}
	}

	public String getAppId() {
		return appId;
	}


	private byte[] buildMessage(String canal, int uid, int expiracion) {

		String uidStr = uid == 0 ? "" : String.valueOf(uid);

		CRC32 crcCanal = new CRC32();
		crcCanal.update(canal.getBytes(StandardCharsets.UTF_8));

		CRC32 crcUid = new CRC32();
		crcUid.update(uidStr.getBytes(StandardCharsets.UTF_8));

		int ts = (int) (System.currentTimeMillis() / 1000);
		int salt = (int) (Math.random() * Integer.MAX_VALUE);

		ByteBuffer buf = new ByteBuffer();
		buf.putInt(PRIVILEGIO_UNIRSE_CANAL);
		buf.putInt(expiracion);
		buf.putString(appId);
		buf.putInt(ts);
		buf.putInt(salt);
		buf.putInt((int) crcCanal.getValue());
		buf.putInt((int) crcUid.getValue());

		return buf.toBytes();
	}

	private String buildToken(byte[] firma, String canal, int uid, int expiracion) {

		String uidStr = uid == 0 ? "" : String.valueOf(uid);

		CRC32 crcCanal = new CRC32();
		crcCanal.update(canal.getBytes(StandardCharsets.UTF_8));

		CRC32 crcUid = new CRC32();
		crcUid.update(uidStr.getBytes(StandardCharsets.UTF_8));

		int ts = (int) (System.currentTimeMillis() / 1000);
		int salt = (int) (Math.random() * Integer.MAX_VALUE);

		ByteBuffer pack = new ByteBuffer();
		pack.putInt(PRIVILEGIO_UNIRSE_CANAL);
		pack.putInt(expiracion);

		ByteBuffer token = new ByteBuffer();
		token.putBytes(firma);
		token.putInt(ts);
		token.putInt(salt);
		token.putBytes(pack.toBytes());

		String tokenBase64 = java.util.Base64.getEncoder()
				.encodeToString(token.toBytes());

		return "006" + appId + tokenBase64;
	}

	private byte[] hmacSha256(byte[] clave, byte[] data)
			throws NoSuchAlgorithmException, InvalidKeyException {

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(clave, "HmacSHA256"));
		return mac.doFinal(data);
	}


	private static class ByteBuffer {

		private byte[] buffer = new byte[0];

		void putInt(int valor) {
			byte[] bytes = new byte[] {
					(byte) (valor & 0xFF),
					(byte) ((valor >> 8) & 0xFF),
					(byte) ((valor >> 16) & 0xFF),
					(byte) ((valor >> 24) & 0xFF)
			};
			buffer = concat(buffer, bytes);
		}

		void putString(String s) {
			byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
			putShort((short) bytes.length);
			buffer = concat(buffer, bytes);
		}

		void putShort(short valor) {
			byte[] bytes = new byte[] {
					(byte) (valor & 0xFF),
					(byte) ((valor >> 8) & 0xFF)
			};
			buffer = concat(buffer, bytes);
		}

		void putBytes(byte[] bytes) {
			putShort((short) bytes.length);
			buffer = concat(buffer, bytes);
		}

		byte[] toBytes() {
			return Arrays.copyOf(buffer, buffer.length);
		}

		private byte[] concat(byte[] a, byte[] b) {
			byte[] result = new byte[a.length + b.length];
			System.arraycopy(a, 0, result, 0, a.length);
			System.arraycopy(b, 0, result, a.length, b.length);
			return result;
		}
	}

}
