package com.game.vcontrolapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

public class FileHandler {
	Context mContext;
	
	public FileHandler(Context context) {
		mContext = context;
	}
	
	public List<String> readLinesFromFile(String file)
	{
		List<String> lines = new ArrayList<String>();
		try {
			FileInputStream stream = mContext.openFileInput(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			do
			{
				line = reader.readLine();
				if(line == null)
					break;
				
				lines.add(line);
			}
			while(true);
			reader.close();
			stream.close();
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public void writeLinesToFile(String file, List<String> lines)
	{
		try {
			FileOutputStream stream = mContext.openFileOutput(file, Activity.MODE_PRIVATE);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
			
			for(String line : lines)
			{
				writer.write(line);
			}
			writer.close();
			stream.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
