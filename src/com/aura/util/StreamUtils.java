package com.aura.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {
	
	public static String sap(InputStream input, String...ignore) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String understood = "";
			String line = null;
			while((line = reader.readLine()) != null) {
				boolean skip = false;
				line = line.trim();
				if(line.isEmpty()) {
					continue;
				}
				for(String i : ignore) {
					if(line.toLowerCase().startsWith(i.toLowerCase())) {
						skip = true;
						break;
					}
				}
				if(skip) {
					continue;
				}
				understood += line;
				understood += "\n";
			}
			return understood.trim();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
