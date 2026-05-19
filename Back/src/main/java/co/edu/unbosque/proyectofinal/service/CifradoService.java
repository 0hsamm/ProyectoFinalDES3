package co.edu.unbosque.proyectofinal.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado de proteger informacion sensible de conversaciones
 * y mensajes usando una frase secreta acordada por los usuarios.
 */
@Service
public class CifradoService {

	private static final String PREFIJO_HASH = "PBKDF2";

	private static final String SEPARADOR = "$";

	private static final int ITERACIONES = 120000;

	private static final int LONGITUD_SALT = 16;

	private static final int LONGITUD_IV = 12;

	private static final int LONGITUD_CLAVE_BITS = 256;

	private static final int LONGITUD_TAG_BITS = 128;

	private SecureRandom secureRandom =
			new SecureRandom();

	/**
	 * Genera un hash de la frase secreta. No se guarda la frase real.
	 *
	 * @param fraseSecreta frase acordada por los participantes
	 * @return hash con formato PBKDF2
	 */
	public String generarHashFrase(
			String fraseSecreta) {

		validarFrase(fraseSecreta);

		byte[] salt =
				generarBytesAleatorios(
						LONGITUD_SALT);

		byte[] hash =
				derivarBytes(
						fraseSecreta,
						salt);

		return PREFIJO_HASH
				+ SEPARADOR
				+ ITERACIONES
				+ SEPARADOR
				+ Base64.getEncoder().encodeToString(salt)
				+ SEPARADOR
				+ Base64.getEncoder().encodeToString(hash);
	}

	/**
	 * Verifica si la frase enviada coincide con el hash guardado.
	 *
	 * @param fraseSecreta frase enviada por el usuario
	 * @param hashGuardado hash almacenado en la conversacion
	 * @return true si coincide
	 */
	public boolean validarFrase(
			String fraseSecreta,
			String hashGuardado) {

		if (!tieneTexto(fraseSecreta)
				|| !esHashFraseConfigurado(hashGuardado)) {

			return false;
		}

		String[] partes =
				hashGuardado.split("\\$");

		if (partes.length != 4) {
			return false;
		}

		byte[] salt =
				Base64.getDecoder().decode(partes[2]);

		byte[] hashEsperado =
				Base64.getDecoder().decode(partes[3]);

		byte[] hashCalculado =
				derivarBytes(
						fraseSecreta,
						salt);

		return comparacionConstante(
				hashEsperado,
				hashCalculado);
	}

	/**
	 * Cifra texto con AES-GCM usando una clave derivada de la frase secreta.
	 *
	 * @param textoPlano mensaje original
	 * @param fraseSecreta frase acordada por los usuarios
	 * @param hashGuardado hash de la frase guardado en conversacion
	 * @return texto cifrado e IV
	 */
	public ResultadoCifrado cifrar(
			String textoPlano,
			String fraseSecreta,
			String hashGuardado) {

		if (!validarFrase(fraseSecreta, hashGuardado)) {
			throw new IllegalArgumentException(
					"Frase secreta invalida");
		}

		try {
			byte[] iv =
					generarBytesAleatorios(
							LONGITUD_IV);

			Cipher cipher =
					Cipher.getInstance("AES/GCM/NoPadding");

			cipher.init(
					Cipher.ENCRYPT_MODE,
					construirClave(fraseSecreta, hashGuardado),
					new GCMParameterSpec(
							LONGITUD_TAG_BITS,
							iv));

			byte[] cifrado =
					cipher.doFinal(
							textoPlano.getBytes(
									StandardCharsets.UTF_8));

			return new ResultadoCifrado(
					Base64.getEncoder()
							.encodeToString(cifrado),
					Base64.getEncoder()
							.encodeToString(iv));

		} catch (Exception e) {
			throw new IllegalStateException(
					"No fue posible cifrar el mensaje",
					e);
		}
	}

	/**
	 * Descifra un mensaje previamente cifrado con AES-GCM.
	 *
	 * @param textoCifrado contenido cifrado en Base64
	 * @param ivBase64 vector inicial en Base64
	 * @param fraseSecreta frase acordada
	 * @param hashGuardado hash guardado de la conversacion
	 * @return texto original
	 */
	public String descifrar(
			String textoCifrado,
			String ivBase64,
			String fraseSecreta,
			String hashGuardado) {

		if (!validarFrase(fraseSecreta, hashGuardado)) {
			throw new IllegalArgumentException(
					"Frase secreta invalida");
		}

		try {
			Cipher cipher =
					Cipher.getInstance("AES/GCM/NoPadding");

			cipher.init(
					Cipher.DECRYPT_MODE,
					construirClave(fraseSecreta, hashGuardado),
					new GCMParameterSpec(
							LONGITUD_TAG_BITS,
							Base64.getDecoder().decode(ivBase64)));

			byte[] plano =
					cipher.doFinal(
							Base64.getDecoder()
									.decode(textoCifrado));

			return new String(
					plano,
					StandardCharsets.UTF_8);

		} catch (Exception e) {
			throw new IllegalArgumentException(
					"No fue posible descifrar el mensaje",
					e);
		}
	}

	public boolean esHashFraseConfigurado(
			String hashGuardado) {

		return tieneTexto(hashGuardado)
				&& hashGuardado.startsWith(
						PREFIJO_HASH + SEPARADOR);
	}

	private SecretKeySpec construirClave(
			String fraseSecreta,
			String hashGuardado) {

		String[] partes =
				hashGuardado.split("\\$");

		if (partes.length != 4) {
			throw new IllegalArgumentException(
					"Hash de frase secreta invalido");
		}

		byte[] salt =
				Base64.getDecoder()
						.decode(partes[2]);

		byte[] key =
				derivarBytes(
						fraseSecreta,
						salt);

		return new SecretKeySpec(
				key,
				"AES");
	}

	private byte[] derivarBytes(
			String fraseSecreta,
			byte[] salt) {

		try {
			SecretKeyFactory factory =
					SecretKeyFactory.getInstance(
							"PBKDF2WithHmacSHA256");

			KeySpec spec =
					new PBEKeySpec(
							fraseSecreta.toCharArray(),
							salt,
							ITERACIONES,
							LONGITUD_CLAVE_BITS);

			return factory
					.generateSecret(spec)
					.getEncoded();

		} catch (Exception e) {
			throw new IllegalStateException(
					"No fue posible derivar la clave de seguridad",
					e);
		}
	}

	private byte[] generarBytesAleatorios(
			int longitud) {

		byte[] bytes =
				new byte[longitud];

		secureRandom.nextBytes(bytes);

		return bytes;
	}

	private void validarFrase(
			String fraseSecreta) {

		if (!tieneTexto(fraseSecreta)
				|| fraseSecreta.trim().length() < 8) {

			throw new IllegalArgumentException(
					"La frase secreta debe tener minimo 8 caracteres");
		}
	}

	private boolean tieneTexto(
			String valor) {

		return valor != null
				&& !valor.trim().isEmpty();
	}

	private boolean comparacionConstante(
			byte[] a,
			byte[] b) {

		if (a.length != b.length) {
			return false;
		}

		int resultado = 0;

		for (int i = 0; i < a.length; i++) {
			resultado |= a[i] ^ b[i];
		}

		return resultado == 0;
	}

	public static class ResultadoCifrado {

		private String textoCifrado;

		private String iv;

		public ResultadoCifrado(
				String textoCifrado,
				String iv) {

			this.textoCifrado = textoCifrado;
			this.iv = iv;
		}

		public String getTextoCifrado() {
			return textoCifrado;
		}

		public String getIv() {
			return iv;
		}
	}
}
