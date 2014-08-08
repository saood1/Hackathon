package com.hackathon.filesync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class FileSync {
	private static final int PORT_NO = 15123;

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		final FileSync f = new FileSync();
		Thread server = new Thread(){
			public void run(){
				f.startServerSocket(PORT_NO);		
			}
		};
		
		server.start();
		f.testClient("/Users/mohamed.khan/Downloads/AutoUpdater.log", PORT_NO);
	}
	
	public void startServerSocket(int portNum){
		try {
			ServerSocket serverSocket = new ServerSocket(portNum);
			FileOutputStream fos = null;
		    BufferedOutputStream bos = null;
		    InputStream is = null;
		    
		    int count = 0;
			int bufferSize = 0;
			
			try{
				while(true){
					Socket socket = serverSocket.accept(); 
					System.out.println("Accepted connection : " + socket); 
					
					is = socket.getInputStream();
			        bufferSize = socket.getReceiveBufferSize();
			        System.out.println("Buffer size: " + bufferSize);
			        
			        fos = new FileOutputStream("/Users/mohamed.khan/AutoUpdater.log");
			        bos = new BufferedOutputStream(fos);
			        
			        byte[] bytes = new byte[bufferSize];
			        
			        while ((count = is.read(bytes)) > 0) {
			            bos.write(bytes, 0, count);
			        }
			    
			        bos.flush();
			        bos.close();
			        is.close();
				}	
			}
			catch (IOException e){
				System.out.println("An IOException occured " + e.getMessage());
			}
			finally{
				serverSocket.close();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void testClient(String filePath, int portNo) throws UnknownHostException, IOException, InterruptedException{
		Socket socket = new Socket("127.0.0.1", portNo);
		
		while(socket.isConnected()==false){
			System.out.println("Waiting for the socket to open..");
			TimeUnit.SECONDS.sleep(5);
		}
		
		System.out.println("Connected");
			
		File file = new File(filePath);
	    
		long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        System.out.println("File is too large.");
	    }
	    
	    byte[] bytes = new byte[(int) length];
	    FileInputStream fis = new FileInputStream(file);
	    BufferedInputStream bis = new BufferedInputStream(fis);
	    BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

	    int count;

	    while ((count = bis.read(bytes)) > 0) {
	        out.write(bytes, 0, count);
	    }

	    out.flush();
	    out.close();
	    fis.close();
	    bis.close();
	    socket.close();
	}
}
