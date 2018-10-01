package com.acxiom.utils;

import static java.lang.String.format;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

public class Utils {
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String objectToJson(Object data) {
		JsonConfig jsonConfig = new JsonConfig();
		PropertyFilter filter = new PropertyFilter() {
			public boolean apply(Object object, String fieldName, Object fieldValue) {
				return null == fieldValue;
			}
		};
		jsonConfig.setJsonPropertyFilter(filter);

		JSONObject json = JSONObject.fromObject(data, jsonConfig);

		return json.toString();
	}

	public static String listToJson(Object data) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray json = JSONArray.fromObject(data, jsonConfig);

		return json.toString();
	}

	public static String getCurrentDate() {
		Date d = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);

	}

	public static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException {
		final Reader pemReader = new StringReader(new String(data));

		final PrivateKeyInfo pemPair;
		try (PEMParser pemParser = new PEMParser(pemReader)) {
			pemPair = (PrivateKeyInfo) pemParser.readObject();
		}

		PrivateKey privateKey = new JcaPEMKeyConverter()
				.setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()).getPrivateKey(pemPair);

		return privateKey;
	}

	public static File findFileSk(File directory) {

		File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

		if (null == matches) {
			throw new RuntimeException(
					format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
		}

		if (matches.length != 1) {
			throw new RuntimeException(format("Expected in %s only 1 sk file but found %d",
					directory.getAbsoluteFile().getName(), matches.length));
		}

		return matches[0];

	}

	public static void out(String format, Object... args) {

		System.err.flush();
		System.out.flush();

		System.out.println(format(format, args));
		System.err.flush();
		System.out.flush();

	}

	public static InputStream generateTarGzInputStream(File src, String pathPrefix) throws IOException {
		File sourceDirectory = src;

		ByteArrayOutputStream bos = new ByteArrayOutputStream(500000);

		String sourcePath = sourceDirectory.getAbsolutePath();

		TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(
				new GzipCompressorOutputStream(new BufferedOutputStream(bos)));
		archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

		try {
			Collection<File> childrenFiles = org.apache.commons.io.FileUtils.listFiles(sourceDirectory, null, true);

			ArchiveEntry archiveEntry;
			FileInputStream fileInputStream;
			for (File childFile : childrenFiles) {
				String childPath = childFile.getAbsolutePath();
				String relativePath = childPath.substring((sourcePath.length() + 1), childPath.length());

				if (pathPrefix != null) {
					relativePath = Utils.combinePaths(pathPrefix, relativePath);
				}

				relativePath = FilenameUtils.separatorsToUnix(relativePath);

				archiveEntry = new TarArchiveEntry(childFile, relativePath);
				fileInputStream = new FileInputStream(childFile);
				archiveOutputStream.putArchiveEntry(archiveEntry);

				try {
					IOUtils.copy(fileInputStream, archiveOutputStream);
				} finally {
					IOUtils.closeQuietly(fileInputStream);
					archiveOutputStream.closeArchiveEntry();
				}
			}
		} finally {
			IOUtils.closeQuietly(archiveOutputStream);
		}

		return new ByteArrayInputStream(bos.toByteArray());
	}

	public static String combinePaths(String first, String... other) {
		return Paths.get(first, other).toString();
	}
}
