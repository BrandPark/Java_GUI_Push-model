package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
	private ServerSocket listener = null;
	private Socket socket = null;
	private ArrayList<Socket> sockets = new ArrayList<>();
	
	@Override
	public void run() {
		System.out.println("서버 실행...");
		System.out.println("클라이언트 접속 대기...");
		try {
			listener = new ServerSocket(9997);
		} catch (IOException e) {
			errorHandler(e.getMessage());
		}
		try {
			while(true) {
				socket = listener.accept();
				System.out.println("클라이언트 접속");
				
				sockets.add(socket);
				new ServiceThread(socket).start();
			}
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				errorHandler(e1.getMessage());
			}
			return;
		}
	}
	
	class ServiceThread extends Thread {
		private Socket socket = null;
		private BufferedReader in = null;
		private BufferedWriter out = null;
		
		public ServiceThread(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {		
			while(true) {
				try {
					this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String inputMsg = in.readLine();
					System.out.println("클라이언트 메세지 : " + inputMsg);
					sendToClients(inputMsg);
					System.out.println("클라이언트들에게 전송 성공...");
				} catch (IOException e) {
					System.out.println("클라이언트 접속 종료...!");
					sockets.remove(socket);
					return;
				}	
			}	
		}
		
		public void sendToClients(String msg) {
			try {
				for(Socket socket : sockets) {
					this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					out.write(msg+"\n");
					out.flush();
				}
			} catch (IOException e) {
				errorHandler(e.getMessage());
			}
		}
		
	}
	
	public void errorHandler(String errorMessage) {
		System.out.println(errorMessage);
		System.exit(1);
	}
	

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}

	

}
