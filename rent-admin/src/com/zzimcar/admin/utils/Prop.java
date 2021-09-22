package com.zzimcar.admin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service("prop")
public class Prop {
	
	private Properties _properties;

	public void setPropFile(String proFile) throws IOException {

		FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
		Resource propResource = fileSystemResourceLoader.getResource(proFile);
		InputStream is = propResource.getInputStream();

		_properties = new Properties();
		_properties.load(is);
	}

	public String get(String key) {
		return _properties.getProperty(key);
	}
}