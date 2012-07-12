package jboost.examples;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImageDataProcess extends DataProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2468159356432685372L;

	private ProWindowStyle[] windowStyle = {
			ProWindowStyle.LeftRight,
			ProWindowStyle.TopBottle,
			ProWindowStyle.TripleHor,
			ProWindowStyle.TripleVet,
			ProWindowStyle.CrossDiff
	};
	
	private StringBuffer stringBuffer = new StringBuffer(num_attr * 8);
	
	BufferedImage image = null;
	
	File specFile = null;
	
	String bufferedSpecFile = null;
	
	int[][] intergrateMap = null;
	
	public ImageDataProcess() {
		// TODO Auto-generated constructor stub
	}
	
	public ImageDataProcess(String specFile) throws IOException {
		super(specFile);
		this.bufferedSpecFile = specFile;
		// TODO Auto-generated constructor stub
	}

	
	private void calcauteIntergrate(byte[] data) {
		int line = 0;
		
		for(int row = 0; row < image.getWidth(); row++) {
			line += data[row];
			intergrateMap[0][row] = line;
		}
		
		for(int col = 1; col < image.getHeight(); col++) {
			line = 0;
			for(int row = 0; row < image.getWidth(); row++) {
				line += data[row + col*image.getWidth()];
				intergrateMap[col][row] = intergrateMap[col-1][row] + line;
			}
		}
	}
	

	private String dataToSpecTrain(byte[] data,int width,int height) {
		// TODO Auto-generated method stub
		
		@SuppressWarnings("unused")
		StringBuffer sb = new StringBuffer(this.num_attr);
		calcauteIntergrate(data);
		return processWindows();
	}
	
	public String dataToSpecTrain(BufferedImage bimage) {
		if(this.image == null) {
			intergrateMap = new int[bimage.getHeight()][bimage.getWidth()];
		}
		
//		if(image.getWidth() != bimage.getWidth() || image.getHeight() != bimage.getHeight()) {
//			System.out.println("File do NOT compatiable!");
//			System.exit(-1);
//		}
		
		this.image = bimage;
		ensureSpecFile();
		byte[] data = ((DataBufferByte)image.getData().getDataBuffer()).getData();
		
		return dataToSpecTrain(data,image.getWidth(),image.getHeight());
	}
	
	@Override
	public String dataToSpecTrain(String imageFile) {
		File file = new File(imageFile);
		System.out.println(file.getName());
		return dataToSpecTrain(file);
	}

	@Override
	public String dataToSpecTrain(File file) {
		// TODO Auto-generated method stub
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(bi.getHeight());
		return dataToSpecTrain(bi);
	}
	
	private String processWindows() {
		Window window = new Window(2, 2);
		window.setLimit(image.getWidth(), image.getHeight());
		
		for(int styleIndex = 0; styleIndex < windowStyle.length;styleIndex++) {
			int xNum = windowStyle[styleIndex].getXnum() * 5;
			int yNum = windowStyle[styleIndex].getYnum() * 5;
			
			for(int winWidth = 2*xNum; 
					winWidth <= image.getWidth(); 
					winWidth += xNum) {
				
				for(int winHeight = 2*yNum;
						winHeight <= image.getHeight(); 
						winHeight += yNum) {
					
					window.setWindow(winWidth, winHeight);
					for(; window.getBottom() <= window.getYlimit();) {
						
						for(; window.getRight() <= window.getXlimit();) {
							
							String tmp = String.valueOf(windowStyle[styleIndex].process(intergrateMap,window));
							//stringBuffer.ensureCapacity(stringBuffer.length() + tmp.length() + 1);
							//System.out.println("tmp " + tmp);
							stringBuffer.append(tmp);
							stringBuffer.append(",");
							
							
							window.moveToward(Direction.Right, 2*xNum);
						}
						
						window.winReturn();
						window.moveToward(Direction.Down, 2*yNum);
						
					}
					//System.out.println("win_" + windowStyle[styleIndex].toString() 
					//		+ "_width_" + winWidth + "_height_" + winHeight + " complete");
				}
			}
			System.gc();
		}
		stringBuffer.ensureCapacity(stringBuffer.length() + 3);
		stringBuffer.append("+1;");
//		stringBuffer.setCharAt(stringBuffer.length() - 1, ';');
		
		stringBuffer.append("\r\n");
		
		return stringBuffer.toString();
	}

	@Override
	public String dataToSpecTrain(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}


	public String getSpecFile(BufferedImage bimage) {
		if(bufferedSpecFile != null)
			return bufferedSpecFile;
		
		System.out.println("start getSpecFile");
		this.image = bimage;
		genSpecFile();
		return bufferedSpecFile;
	}
	
	private void ensureSpecFile() {
		if(bufferedSpecFile != null)
			return;
		genSpecFile();
	}
	
	public void setSpecFile(String specFile) {
		
	}
	
	private void genSpecFile() {
		// TODO Auto-generated method stub
		if(bufferedSpecFile != null)
			return;
		
		int attr_tmp = 0;
		StringBuffer sb = new StringBuffer(1024 * 512);
		
		sb.append("exampleTerminator=;\r\n" + "attributeTerminator=,\r\n"
					+ "maxBadExa=0\r\n");
		
		Window window = new Window(2, 2);
		window.setLimit(image.getWidth(), image.getHeight());
		
		for(int styleIndex = 0; styleIndex < windowStyle.length;styleIndex++) {
			int xNum = windowStyle[styleIndex].getXnum() * 5;
			int yNum = windowStyle[styleIndex].getYnum() * 5;
			
			for(int winWidth = 2 * xNum; 
					winWidth <= image.getWidth(); 
					winWidth += xNum) {
				
				for(int winHeight = 2 * yNum;
						winHeight <= image.getHeight(); 
						winHeight += yNum) {
					
					window.setWindow(winWidth, winHeight);
					for(; window.getBottom() <= window.getYlimit();) {
						
						for(; window.getRight() <= window.getXlimit();) {
							attr_tmp++;
							String str = "win_" + windowStyle[styleIndex].toString() 
									+ "_width_" + winWidth + "_height_" + winHeight
									+ "_pos_" + window.getLeft() + "_" + window.getTop()
									+ "\t\t\t" + "number" + "\r\n";
							//System.out.println(str);
							sb.ensureCapacity(sb.length() + str.length());
							sb.append(str);
							
							window.moveToward(Direction.Right, 2 * xNum);
						}
						
						window.winReturn();
						window.moveToward(Direction.Down, 2*yNum);
					}
					System.out.println("win_" + windowStyle[styleIndex].toString() 
							+ "_width_" + winWidth + "_height_" + winHeight + " complete");
				}
			}
		}
		super.num_attr = attr_tmp;
		sb.append("labels (+1,-1)\r\n\r\n");
		bufferedSpecFile = sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		if(args.length < 1) {
			System.out.println(args[0]);
			System.out.println("No file input!");
			System.exit(-1);
		}
		
		System.out.println("File is " + args[0]);
		File file = new File(args[0]);
		BufferedImage bimage = ImageIO.read(file);
		System.out.println("Image " + bimage.getWidth() + " " + bimage.getHeight());
		
		
		ImageDataProcess process = new ImageDataProcess();
		String str = process.getSpecFile(bimage);
		
		File write = new File(args[0] + ".spec");
		FileWriter fw = new FileWriter(write);
		fw.write(str);
		fw.close();
	}
}
