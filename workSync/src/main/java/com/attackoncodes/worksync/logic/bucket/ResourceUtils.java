package com.attackoncodes.worksync.logic.bucket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Component;

import com.attackoncodes.worksync.exception.server.InternalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class ResourceUtils {

	private final ObjectMapper objectMapper;
	
	public byte[] bytesToJson(Object generic) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(generic);
		} catch (JsonProcessingException e) {
			throw new InternalException("Failed to process user data for export.", e);
		}
	}
	
	public byte[] resourceToBytes(byte[] data, String filename) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos)) {
			
			zos.setLevel(Deflater.BEST_COMPRESSION);

			ZipEntry entry = new ZipEntry(filename);
			
			zos.putNextEntry(entry);
			zos.write(data);
			zos.closeEntry();
			return baos.toByteArray();
		} catch (IOException e) {
			throw new InternalException("Failed to create zip archive for user data.", e);
		}
	}

}
