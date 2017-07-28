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
			// 특정 port 로 소켓을 열어서 서버를 생성
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		
		System.out.println("Server is running");
		
		try {
			// 서버의 메인 thread 는 클라이언트와의 연결만 담당하고, 
			// 실제 task 는 서버 thread 에서 처리되기 때문에 동시에 많은 요청을 처리할 수 있게 된다.
			while(true){
				Socket client = serverSocket.accept();
				processClient(client); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processClient(Socket client){
		// 태스크를 서브 thread 로 실행해서 서버소켓이 계속 동작할 수 있도록 해준다.
		new Thread(){
			public void run(){
				// 1. try-with 문은 자동으로 스트림을 닫아준다.
				InputStream is = null;
				OutputStream os = null;
				
				try { // 연결된 Socket 과 Stream 을 열어서 통신 준비를 한다
					is = client.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is)); // 버퍼로 데이터 처리속도를 향상시킨다
					String line = "";
					// 데이터가 없을 때까지 계속 읽는다
					while((line = br.readLine()) != null){
						System.out.println(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				// 2. 응답을 하는 Stream 을 열어서 응답을 완료한다.
				try {
					os = client.getOutputStream();
					String message = "Response Completed";
					// 헤더
					os.write("HTTP/1.0 200 OK\r\n".getBytes());
					os.write("Content-Type: text/html\r\n".getBytes());
					// 헤더와 바디의 구분줄
					os.write("\r\n".getBytes());
					// 실제 바디 메시지
					os.write(message.getBytes());
					os.flush();
				} catch(Exception e){
					e.printStackTrace();
				}
				
				// 3. 요청처리가 되면 
				try{
					client.close();
				} catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}.start();
	}

}
