package org.sentrysoftware.metricshub.engine.security;

/*-
 * ╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲
 * MetricsHub Engine
 * ჻჻჻჻჻჻
 * Copyright 2023 - 2024 Sentry Software
 * ჻჻჻჻჻჻
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityManager {

	private static final char[] KEY_STORE_PASSWORD = new char[] {
		'M',
		'a',
		't',
		'r',
		'i',
		'x',
		',',
		' ',
		'C',
		'r',
		'e',
		'd',
		'i',
		't',
		's',
		':',
		' ',
		'B',
		'e',
		'r',
		't',
		'r',
		'a',
		'n',
		'd',
		',',
		' ',
		'E',
		'l',
		'v',
		'i',
		's',
		',',
		' ',
		'F',
		'a',
		'd',
		'h',
		'e',
		'l',
		'a',
		',',
		' ',
		'H',
		'u',
		'a',
		'n',
		',',
		' ',
		'N',
		'a',
		's',
		's',
		'i',
		'm',
		',',
		' ',
		'R',
		'a',
		'm',
		'a',
		's',
		's',
		'h',
		' ',
		'a',
		'n',
		'd',
		' ',
		'T',
		'h',
		'o',
		'm',
		'a',
		's'
	};
	private static final String MASTER_KEY_ALIAS = "masterKey";
	public static final String METRICSHUB_KEY_STORE_FILE_NAME = "metricshub-keystore.p12";

	/**
	 * Encrypt the given password
	 *
	 * @param passwd       Password to encrypt
	 * @param keyStoreFile The key store holding secret information
	 * @return char array
	 * @throws MetricsHubSecurityException
	 */
	public static char[] encrypt(final char[] passwd, @NonNull final File keyStoreFile)
		throws MetricsHubSecurityException {
		if (passwd == null) {
			return passwd;
		}

		return CryptoCipher.encrypt(passwd, getSecretKey(keyStoreFile));
	}

	/**
	 * Decrypt the password
	 *
	 * @param encrypted    The encrypted text
	 * @param keyStoreFile The key store holding the secret information
	 * @return char array
	 * @throws MetricsHubSecurityException
	 */
	public static char[] decrypt(final char[] encrypted, final File keyStoreFile) throws MetricsHubSecurityException {
		if (encrypted != null && keyStoreFile != null && keyStoreFile.exists()) {
			return CryptoCipher.decrypt(encrypted, getSecretKey(keyStoreFile));
		}

		return encrypted;
	}

	/**
	 * Get the secret key from the KeyStore
	 *
	 * @param keyStoreFile The key store file
	 *
	 * @return {@link SecretKey} instance
	 * @throws MetricsHubSecurityException
	 */
	private static SecretKey getSecretKey(@NonNull final File keyStoreFile) throws MetricsHubSecurityException {
		// Load the keyStore
		final KeyStore ks = loadKeyStore(keyStoreFile);

		// Get the secretKey entry by its alias
		KeyStore.SecretKeyEntry entry = null;
		try {
			entry = (KeyStore.SecretKeyEntry) ks.getEntry(MASTER_KEY_ALIAS, new PasswordProtection(KEY_STORE_PASSWORD));
		} catch (Exception e) {
			throw new MetricsHubSecurityException("Error detected when getting the secret key entry", e);
		}

		// No entry, means the master key is not generated yet, let's generate a new one
		if (entry == null) {
			return generateMasterKey(ks, KEY_STORE_PASSWORD, keyStoreFile);
		}

		return entry.getSecretKey();
	}

	/**
	 * Load the PRODUCT-CODE-keystore.p12 file, if the keyStore file doesn't exist then it is created
	 *
	 * @param keyStoreFile The key store file
	 * @throws MetricsHubSecurityException
	 */
	public static KeyStore loadKeyStore(@NonNull final File keyStoreFile) throws MetricsHubSecurityException {
		try {
			final KeyStore ks = KeyStore.getInstance("PKCS12");

			if (keyStoreFile.exists()) {
				// if exists, load
				try (FileInputStream stream = new FileInputStream(keyStoreFile)) {
					ks.load(stream, KEY_STORE_PASSWORD);
				}
			} else {
				// if not exists, create
				ks.load(null, null);

				// Store the key store password
				try (FileOutputStream stream = new FileOutputStream(keyStoreFile)) {
					ks.store(stream, KEY_STORE_PASSWORD);
				}
			}

			return ks;
		} catch (Exception e) {
			throw new MetricsHubSecurityException("Error detected when loading " + METRICSHUB_KEY_STORE_FILE_NAME, e);
		}
	}

	/**
	 * Generate and save a new master key in the given {@link KeyStore}
	 *
	 * @param ks           The keyStore holding the secret key
	 * @param password     The password used to protect the {@link KeyStore}
	 * @param keyStoreFile The key store file (PRODUCT-CODE-keystore.p12)
	 * @throws MetricsHubSecurityException
	 */
	public static SecretKey generateMasterKey(
		@NonNull final KeyStore ks,
		@NonNull final char[] password,
		@NonNull final File keyStoreFile
	) throws MetricsHubSecurityException {
		try {
			// Random master key
			char[] masterKey = CryptoCipher.generateRandomMasterKey();

			// Create the KeySpec for password based encryption (PBE)
			final PBEKeySpec spec = new PBEKeySpec(masterKey, CryptoCipher.getSalt(), 2333, 32 * 8);

			// Get the secrete key factory
			final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

			// Generate a secret key
			final SecretKey secretKey = skf.generateSecret(spec);

			// Create a secret key entry to be stored in the KeyStore
			KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(secretKey);

			// Set the secret key entry
			ks.setEntry(MASTER_KEY_ALIAS, skEntry, new PasswordProtection(password));

			// Finally commit
			ks.store(new FileOutputStream(keyStoreFile), password);

			return secretKey;
		} catch (Exception e) {
			throw new MetricsHubSecurityException("Error detected when generating the master key", e);
		}
	}
}
