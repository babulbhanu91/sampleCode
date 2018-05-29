package com.main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FindReplace {
	
	private static String absolutePath;
	private static String findText;
	private static String replaceText;
	
	public FindReplace(String path, String find, String replace) {
		absolutePath = path;
		findText = find;
		replaceText = replace;
	}
	
	public static JSONArray findAndReplace() {
		File file = new File(absolutePath);
		return traverseDirs(file);
	}
	
	private static JSONArray traverseDirs(File source) {
		JSONArray output = new JSONArray();
		File[] files = source.listFiles();
		for(File f : files) {
			if(f.isDirectory())
				traverseDirs(f);
			else {
				JSONArray arr = findReplaceUtil(f);
				if(arr.size() > 0) {
					JSONObject obj = new JSONObject();
					obj.put(f.getAbsolutePath(), arr);
					output.add(obj);
				}
			}
		}
		return output;
	}
	
	private static JSONArray findReplaceUtil(File file) {
		JSONArray arr = new JSONArray();
		File dst = new File(file.getAbsolutePath() + ".bak");
		try {
			String str = "";
			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedWriter bw = new BufferedWriter(new FileWriter(dst));
			int line = 0;
			while((str = br.readLine()) != null) {
				line++;
				JSONArray cols = new JSONArray();
				int lastIndex = 0;
				while (lastIndex != -1) {
				    lastIndex = str.indexOf(findText, lastIndex);
				    if (lastIndex != -1) {
				    	cols.add(Integer.toString(lastIndex));
				        lastIndex += findText.length();
				    }
				}
				str = str.replaceAll(findText, replaceText) + "\n";
				bw.write(str);
				if(cols.size() > 0) {
					JSONObject row = new JSONObject();
					row.put(line, cols);
					arr.add(row);
				}
			}
			br.close();
			bw.close();
			file.delete();
			dst.renameTo(file);
		} catch(FileNotFoundException fe) {
			fe.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		return arr;
	}

	

}
