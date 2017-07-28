import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Server {
	
	ServerSocket serverSocket;
	
	public Server(int port){
		try {
			// Ư�� port �� ������ ��� ������ ����
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		
		System.out.println("Server is running");
		
		try {
			// ������ ���� thread �� Ŭ���̾�Ʈ���� ���Ḹ ����ϰ�, 
			// ���� task �� ���� thread ���� ó���Ǳ� ������ ���ÿ� ���� ��û�� ó���� �� �ְ� �ȴ�.
			while(true){
				Socket client = serverSocket.accept();
				processClient(client); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processClient(Socket client){
		// �½�ũ�� ���� thread �� �����ؼ� ���������� ��� ������ �� �ֵ��� ���ش�.
		new Thread(){
			public void run(){
				// 1. try-with ���� �ڵ����� ��Ʈ���� �ݾ��ش�.
				InputStream is = null;
				OutputStream os = null;
				
				try { // ����� Socket �� Stream �� ��� ��� �غ� �Ѵ�
					is = client.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is)); // ���۷� ������ ó���ӵ��� ����Ų��
					String line = "";
					// �����Ͱ� ���� ������ ��� �д´�
					while((line = br.readLine()) != null){
						System.out.println(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				// 2. ������ �ϴ� Stream �� ��� ������ �Ϸ��Ѵ�.
				try {
					os = client.getOutputStream();
					String message = "Response Completed";
					// ���
					os.write("HTTP/1.0 200 OK\r\n".getBytes());
					os.write("Content-Type: text/html\r\n".getBytes());
					// ����� �ٵ��� ������
					os.write("\r\n".getBytes());
					// ���� �ٵ� �޽���
					os.write(message.getBytes());
					os.flush();
				} catch(Exception e){
					e.printStackTrace();
				}
				
				// 3. ��ûó���� �Ǹ� 
				try{
					client.close();
				} catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}.start();
	}

}
