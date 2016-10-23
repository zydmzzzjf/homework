package jsp;

import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jsp.Request;
import jsp.Response;
import jsp.jspReader;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

public class HttpServer {

	// �رշ�������
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		// �ȴ���������
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			// �������׽��ֶ���
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// ѭ���ȴ�����
		while (true) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				// �ȴ����ӣ����ӳɹ��󣬷���һ��Socket����
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();

				// ����Request���󲢽���
				Request request = new Request(input);
				request.parse();

				// ���� Response ����
				Response response = new Response(output);
				response.setRequest(request);

				// ȡ��url
				String url = request.getUri();

				// ����xml
				/*
				 * Boolean canRead = findurl(url);
				 * 
				 * if(canRead) { 
				 * //����xml�õ����� 
				 * String servletName = getServeletName(url); 
				 * ServletProcessor processor = new ServletProcessor(); 
				 * processor.process(request,response,servletName); 
				 * } 
				 * else { 
				 * StaticResourceProcessor
				 * processor = new StaticResourceProcessor();
				 * processor.process(request, response); }
				 */
				String filename = System.getProperty("user.dir")+ File.separator +"jsp" + File.separator + url;
				if(new File(filename).exists())
				{
					// ת��jsp
					jspReader jsp = new jspReader(url.split("/")[1]);
					ServletProcessor processor = new ServletProcessor(); 
					processor.process(request,response,jsp.getServletName()); 
				}
				else
				{
					System.out.println("�Ҳ������ļ���");
				}
				

				// �ر� socket
				socket.close();

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public Boolean findurl(String url) {
		Element element = null;
		// ����ʹ�þ���·��
		File f = new File("web.xml");

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);

			// �õ�һ��elment��Ԫ��
			element = dt.getDocumentElement();

			// ��ø�Ԫ���µ��ӽڵ�
			NodeList childNodes = element.getChildNodes();

			NodeList theNodeList = null;

			// ������Щ�ӽڵ�
			for (int i = 0; i < childNodes.getLength(); i++) {
				// ���ÿ����Ӧλ��i�Ľ��
				Node node1 = childNodes.item(i);

				// ƥ��ÿһ��servelet-mapping���uri
				if ("servlet-mapping".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("url-pattern".equals(detail.getNodeName())) {
							// ƥ��url
							if (url.equals(detail.getTextContent()))// ���ƥ�䵽������true
								return true;
						}

					}
				}
			}
		}

		catch (Exception e) {
			System.out.println(e.toString());
		}

		return false;
	}

	public String getServeletName(String url) {
		String ServeletName = null;

		Element element = null;
		// ����ʹ�þ���·��
		File f = new File("web.xml");

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);

			// �õ�һ��elment��Ԫ��
			element = dt.getDocumentElement();

			// ��ø�Ԫ���µ��ӽڵ�
			NodeList childNodes = element.getChildNodes();

			NodeList theNodeList = null;

			// ������Щ�ӽڵ�
			for (int i = 0; i < childNodes.getLength(); i++) {
				// ���ÿ����Ӧλ��i�Ľ��
				Node node1 = childNodes.item(i);

				// ƥ��ÿһ��servelet-mapping���uri
				if ("servlet-mapping".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("url-pattern".equals(detail.getNodeName())) {
							// ƥ��url
							if (url.equals(detail.getTextContent()))// ���ƥ�䵽�����ýڵ�ȡ��
								theNodeList = nodeDetail;
						}

					}
				}
			}

			String Sname = null;
			// ȡ������
			for (int j = 0; j < theNodeList.getLength(); j++) {
				Node detail = theNodeList.item(j);

				if ("servlet-name".equals(detail.getNodeName())) // ƥ��name
				{
					Sname = detail.getTextContent();
				}

			}

			NodeList theNodeList1 = null;

			// �ٴα�����ƥ���Ӧservelet-class
			for (int i = 0; i < childNodes.getLength(); i++) {
				// ���ÿ����Ӧλ��i�Ľ��
				Node node1 = childNodes.item(i);

				// ƥ��ÿһ��servelet���uri
				if ("servlet".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("servlet-name".equals(detail.getNodeName())) // ƥ��url��Ӧ����
							if (Sname.equals(detail.getTextContent()))// ���ƥ�䵽�����ýڵ�ȡ��
								theNodeList1 = nodeDetail;
					}
				}
			}
			// ȡ��serveletname
			for (int j = 0; j < theNodeList1.getLength(); j++) {
				Node detail = theNodeList1.item(j);

				if ("servlet-class".equals(detail.getNodeName())) // ƥ��
				{
					ServeletName = detail.getTextContent();
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return ServeletName;
	}
}
