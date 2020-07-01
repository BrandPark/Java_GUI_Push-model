package client;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientFrame extends JFrame {
	private JTextArea ta = new JTextArea();
	private JTextField tf = new JTextField();
	private Socket socket = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	
	public ClientFrame() {
		super("Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		
		ta.setEnabled(false);
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = tf.getText();
				if(!msg.isEmpty())
					try {
						out.write(msg+"\n");
						out.flush();
						tf.setText("");
						
					} catch (IOException e1) {
						errorHandler(e1.getMessage());
					}
			}
		});
		
		c.add(new JScrollPane(ta),BorderLayout.CENTER);
		c.add(tf,BorderLayout.SOUTH);
	
		setSize(300,500);
		setVisible(true);
		
		init();
		
		Thread th = new Thread(new ReceiverThread());
		th.start();
	}
	public void init() {
		
		try {
			ta.append("서버와 연결중...\n");
			socket = new Socket("localhost",9997);
			ta.append("서버와 연결 성공\n\n");
			ta.append("*******************************************\n");
			ta.append("*************Chatting Program**************\n");
			ta.append("*******************************************\n\n");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));	

		} catch (UnknownHostException e) {
			errorHandler(e.getMessage());
		} catch (IOException e) {
			errorHandler(e.getMessage());
		}		
	}
	public void errorHandler(String errorMessage) {
		System.out.println(errorMessage);
		System.exit(1);
	}
	
	class ReceiverThread implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					String receiveMsg = in.readLine();
					ta.append(receiveMsg+"\n");
				} catch (IOException e) {
					errorHandler(e.getMessage());
				}
			}
			
		}
	}

	public static void main(String[] args) {
		new ClientFrame();

	}

}
