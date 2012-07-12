package jboost.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/*
 * 直接用于Boost的数据，即处理过的数据，处理策略（strategy）为process 
 */
public class TrainData implements Runnable{
	
	private String stastic_data = null;//汇总到一个文件
	
	//private ArrayList<String> t_data = new ArrayList<String>(); //每幅图片的特征
	private LinkedList<String> t_data = new LinkedList<String>();
	private String d_data = null;
	private int terms_length = 0;  //分隔符（逗号分号）的总长度，决定于特征数量即图片像素
	private Semaphore sem = new Semaphore(0);
	private BufferedReader br = null;
	private boolean theEnd = false;
	
	private DataProcess process = null; //strategy 模式
	
	private String pathname;
	
	public TrainData(DataProcess dProcess) {
		process = dProcess;
		terms_length = dProcess.getNumAttr();//分隔符与特征数相等
	}
	
	public void setData(String data,int index) {
		t_data.add(index, data);
	}
	
	
	public void addTrainData(String trainFile) {
		//File file = null;
		//file = new File(trainFile);
		
		t_data.add(process.dataToSpecTrain(trainFile));
		System.out.println(trainFile + " added");
	}
	
	public void addTrainData(File trainFile) throws IOException {
		//File file = null;
		//file = new File(trainFile);
		
		//t_data.add(process.dataToSpecTrain(trainFile));
		d_data = process.dataToSpecTrain(trainFile);
		saveData();
		System.out.println(trainFile + " added");
	}
	
	public boolean addTrainDataDir(String trainDir) throws IOException {
		File file = new File(trainDir);
		@SuppressWarnings("unused")
		FileReader fr = null;
		//t_data.get(1).
		if(file.isDirectory()) {
			try {
				fr = new FileReader(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File[] filelist = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					//Exp
					return true;
				}
			});
			for(int i=0;i < filelist.length;i++) {
				if(!filelist[i].isDirectory())
					this.addTrainData(filelist[i]);
			}
			theEnd = true;
			
			return true;
			
		} else {
			System.out.println(trainDir+" is NOT diretory");
			return false;
		}
	}
	
	public BufferedReader toReader() {

		if(br != null) {
			return br;
		}
		
		int length = 0;
		for(int i = 0;i < t_data.size();i++) {
			length += (t_data.get(i).length() + terms_length + 2);//换行符长度为2
		}
		
		StringBuffer sb = new StringBuffer(length);
		for(int i = 0;i < t_data.size();i++) {
			sb.append(t_data.get(i));
			sb.append("\r\n");//换行符
		}
		
		stastic_data = sb.toString();
		br = new BufferedReader(new StringReader(stastic_data), length);
		
		return br;
	}
	
	public void saveData() throws IOException {
		File file = new File(pathname);
		if(file.exists())
			file.delete();
		FileWriter write = new FileWriter(file);
		//write.
		
//		for(int i = 0; i < t_data.size(); i++) {
//			write.append(t_data.get(i));
//		}
		write.append(d_data);
		
		write.close();
	}
	
	public void setSaveData(String name) {
		this.pathname = name;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ImageDataProcess process = new ImageDataProcess(args[0]);
		TrainData train = new TrainData(process);
		System.out.println(args[1]);
		train.setSaveData(args[2]);
		train.addTrainDataDir(args[1]);
		
		
//		Thread thread = new Thread(train);
//		thread.start();
//		
//		thread.join();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		while(theEnd == false) {
			
			try {
				sem.tryAcquire();
				this.saveData();
				//sem.
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
