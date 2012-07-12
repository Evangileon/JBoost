package jboost.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

public abstract class DataProcess implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895667790814853846L;
	protected byte[] data = null;
	protected int num_attr = 0;
	
	public DataProcess() {
		// TODO Auto-generated constructor stub
	}
	
	public DataProcess(String specFile) throws IOException {
		doDataProcess(specFile);
	}
	
	public void doDataProcess(String specFile) throws IOException {
		File file = new File(specFile);
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("No "+specFile+"specialize file!");
			e.printStackTrace();
		}
		
		int lineCount = 0;
		BufferedReader br = new BufferedReader(fr);
		@SuppressWarnings("unused")
		String line="";
        //String[] arrs=null;
        while ((line=br.readLine())!=null) {
            //arrs = line.split(",");
            lineCount++;
            //System.out.println(arrs[0] + " : " + arrs[1] + " : " + arrs[2]);
        }
        
        num_attr = lineCount;
        br.close();
        fr.close();
    
	}
	
	public int getNumAttr() {
		return num_attr;
	}
	
	//private abstract void genSpecFile();
	
	public abstract String dataToSpecTrain(byte[] data); 
	
	public abstract String dataToSpecTrain(File file);
	
	public abstract String dataToSpecTrain(String file);
	
}

