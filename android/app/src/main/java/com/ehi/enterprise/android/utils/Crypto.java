package com.ehi.enterprise.android.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class used for encrypting and decrypting Strings
 */
public class Crypto {

	private static final String TAG = Crypto.class.getCanonicalName();

	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String ENCRYPTION = "AES";

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final String FILENAME = "wodhwldan.kdf";
	private static final int KEY_LENGTH = 256;
	private static final int IV_LENGTH = 16;

	private SecretKey mSecretKey;
	private final Context mContext;

	public Crypto(final Context context) throws NoSuchAlgorithmException, IOException {
		mContext = context.getApplicationContext();
		mSecretKey = getSecretKey();
	}

	/**
	 * Encrypts a given {@link String}. If you pass null it will be saved as an empty string.
	 *
	 * @param text {@link String} to be encrypted
	 * @return encrypted {@link String}
	 */
	@SuppressWarnings("LocalVariableNamingConvention")
	public final String encrypt(final String text) {
		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			final byte[] iv = generateIv();
			final IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, mSecretKey, ivspec);

			final byte[] ciphertext = cipher.doFinal(TextUtils.isEmpty(text) ? "".getBytes() : text.getBytes("utf-8"));
			return bytesToStorableString(iv) + ':' + bytesToStorableString(ciphertext);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Decrypts a given {@link String}
	 *
	 * @param encrypted {@link String} to be decrypted
	 * @return decrypted {@link String}
	 */
	public final String decrypt(final String encrypted) {
		try {
			return new String(decryptToBytes(mSecretKey, encrypted), "utf-8");
		} catch (final UnsupportedEncodingException e) {
			// Should never happen since the supported encoding types should never change on the
			// device
			throw new RuntimeException(e);
		}
	}

	private SecretKey getSecretKey() throws NoSuchAlgorithmException, IOException {
		if (mSecretKey == null) {
			try {
				mSecretKey = readSecretFromLocalStorage(mContext);
			} catch (final FileNotFoundException | NullPointerException e) {
				// Likely didn't save this yet or it was deleted, so just continue
			}

			if (mSecretKey == null) {
				mSecretKey = generateNewKey();
				writeSecretToLocalStorage(mContext, mSecretKey);
			}
		}

		return mSecretKey;
	}

	private static SecretKey generateNewKey() throws NoSuchAlgorithmException {
		// Generate a 256-bit key
		final int outputKeyLength = KEY_LENGTH;

		// Do *not* seed secureRandom! Automatically seeded from system entropy.
		final KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION);
		keyGenerator.init(outputKeyLength, SECURE_RANDOM);

		return keyGenerator.generateKey();
	}

	private static void writeSecretToLocalStorage(final Context context, final SecretKey secretKey)
			throws IOException {
		final FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
		fos.write(secretKey.getEncoded());
		fos.close();
	}

	private static SecretKey readSecretFromLocalStorage(final Context context) throws IOException {
		final FileInputStream fis = context.openFileInput(FILENAME);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int bytesRead;
		final byte[] buffer = new byte[8192];
		while ((bytesRead = fis.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}

		final byte[] bytes = baos.toByteArray();
		baos.close();
		fis.close();

		return new SecretKeySpec(bytes, 0, bytes.length, ENCRYPTION);
	}

	private static byte[] generateIv() {
		final byte[] bytes = new byte[IV_LENGTH];
		SECURE_RANDOM.nextBytes(bytes);

		return bytes;
	}

	@SuppressWarnings("LocalVariableNamingConvention")
	private static byte[] decryptToBytes(final SecretKey key, final String ciphertext) {
		try {
			final String[] parts = ciphertext.split(":");
			final String iv = parts[0];
			final String ctext = parts[1];
			final IvParameterSpec ivspec = new IvParameterSpec(bytesFromStorableString(iv));
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
			return cipher.doFinal(bytesFromStorableString(ctext));
		} catch (final GeneralSecurityException e) {
			DLog.e("crypto",
					"Unable to decrypt data: " + bytesToStorableString(key.getEncoded()) + " : "
							+ ciphertext, e);
			return new byte[0];
		} catch (final RuntimeException e) {
			return new byte[0];
		}
	}

	@SuppressWarnings("QuestionableName")
	private static byte[] bytesFromStorableString(final String string) {
		return Base64.decode(string, Base64.DEFAULT);
	}

	private static String bytesToStorableString(final byte[] bytes) {
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}
}