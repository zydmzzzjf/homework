import java.io.*;
import java.net.*;
class ServerThread extends Thread  {
	private Socket connectToClient;
	private DataInputStream  infromClient;
	private DataOutputStream  outtoClient;


	//
	public ServerThread (Socket socket) throws IOException {
		super();
		connectToClient=socket;
		infromClient=new DataInputStream (connectToClient.getInputStream());
		outtoClient=new DataOutputStream (connectToClient.getOutputStream());
		start();//

	}
	//
	public void run(){
		try{
			String Str,str;
			boolean goon=true;
			while (goon){
				Str=infromClient.readUTF ();
				if (!Str.equals("bye")){
					System.out.println("接收到的字符串为："+Str);
					str = reverse(Str);
					outtoClient.writeUTF(Str);
					outtoClient.flush();
					System.out.println("字符串"+Str+"已经发送");

				}else{
					goon=false;
					outtoClient.writeUTF("bye");
					outtoClient.flush();

				}
			}

			infromClient.close();
			outtoClient.close();
			connectToClient.close();	
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private String reverse(String str) {
		char[] chars=str.toCharArray();
		int i;
		str="";
		for(i=chars.length-1;i>=0;i--)
		str+=chars[i];
		return str;

	}
}


public  class MultiServer {
	public static void main(String[] args){
		try{
			System.out.println("等待连接");
			ServerSocket serverSocket=new ServerSocket(5800);
			Socket connectToClent=null;
			while(true){
				connectToClent=serverSocket.accept();
				new ServerThread(connectToClent);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
