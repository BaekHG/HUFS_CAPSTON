
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	
	static Map<String, String> loginCookie = null;
	// Windows, Whale�� User Agent.
	static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Whale/1.4.64.6 Safari/537.36";
	
	public static void main(String[] args) throws IOException, Exception {
		// �ڹٽ�ũ��Ʈ function ȣ���� ���� Nashorn JavaScript ���� ���
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval(new FileReader("./src/sha512.js"));
		
		// cast the script engine to an invocable instance
		Invocable invocable = (Invocable) engine;
		//Object result = invocable.invokeFunction("SHA512", "sdfdfsf");
		
		// parameter ������ form data ����
		String sID = "201401483";
		String rawPW = "";
		// �н����� �ؽ��� ����
		String sha512PW = invocable.invokeFunction("SHA512", rawPW).toString();
		//System.out.println(sha512PW);
		
		String loginURL = "https://eclass2.hufs.ac.kr:4443/ilos/lo/login.acl?usr_id=" + sID +
							"&usr_pwd=" + "8b8b6aa4bf808f01017bc1fb50960a18b3861f6ae269b138de8ff975c15b4ab607a2c3847301bb74d1b6ac106d15edbb9d7baf57826bbecbb0b5cc9d9c5948c3";
		
		// �α���(POST) - HTTPS
    	Connection.Response response1 = Jsoup.connect(loginURL)
				.userAgent(userAgent)
				.timeout(3000)
				.header("Origin", "http://eclass2.hufs.ac.kr:8181")
				.header("Referer", "http://eclass2.hufs.ac.kr:8181/ilos/main/member/login_form.acl")
				.header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Connection", "keep-alive")
				.header("Accept-Encoding", "gzip, deflate")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.method(Connection.Method.POST)
				.execute();
		//System.out.println(response1.statusMessage());
		// �α��� ���� �� ���� ��Ű.
		// ��Ű �� TSESSION�̶�� ���� Ȯ���� �� �ִ�.
		loginCookie = response1.cookies();
		
		if(loginCookie.containsKey("JSESSIONID")) {
			System.out.println("JSESSIONID FOUND!");
		}
		else {
			System.out.println("JSESSIONID NOT FOUND!!");
		}
		
		// ��Ŭ���� ���� ������ GET
		Document mainPage = getPageDocument("http://eclass2.hufs.ac.kr:8181/ilos/main/main_form.acl");
		//System.out.println(mainPage.toString());
		
		// �������� �Ľ�
		Elements e1 = mainPage.select("em[class=sub_open]");
		List<String> lectureList = new ArrayList<String>(); // �������� �̸� ����Ʈ
		List<String> lectureCodeList = new ArrayList<String>(); // �������� �ڵ� �� URL ����Ʈ
		
		for(Element lec : e1) {
			String lecRawTitle = lec.attr("title");
			String lecRawCode = lec.attr("onclick"); 
//			System.out.println(lecRawTitle);
//			System.out.println(lecRawCode);
			lectureList.add(lecRawTitle.substring(0, lecRawTitle.length() - 9));
			lectureCodeList.add(lecRawCode.substring(lecRawCode.indexOf('\'') + 1, lecRawCode.lastIndexOf('\'')));
			//System.out.println(lecRawCode.substring(lecRawCode.indexOf('\'') + 1, lecRawCode.lastIndexOf('\'')));
			//System.out.println(lecRawTitle.substring(0, lecRawTitle.length() - 9));
		}
		//System.out.println(lectureList.size());
		//System.out.println(lectureCodeList.size());
		
		for(int i = 0; i < lectureList.size(); i++) {

			// Refresh connection on every lecture
			eclassRoomConnect(lectureCodeList.get(i));
			
	    	// ���ǽ� �������� �Խ��� ������  GET
			Document eclassNoticePage = getPageDocument("http://eclass2.hufs.ac.kr:8181/ilos/st/course/notice_list_form.acl");
			
			// �������� 
			System.out.println("*** [" + lectureList.get(i) + "] �������� �ֱ� �Խù� ***");
			if(!eclassNoticePage.select("table[class=bbslist] tbody tr td[class=left]").isEmpty()) {
				Elements noticeTitles = eclassNoticePage.select("table[class=bbslist] tbody tr");
				// ���� �ֱ� �������� �Խù��� ����� �Խ� ���� ���
				System.out.println("===> " + noticeTitles.get(0).select("td").get(1).text() + "\t" + 
									noticeTitles.get(0).select("td").get(4).text());	
			}
			else {
				System.out.println("===> ���������� �����ϴ�..");
			}
			System.out.println();
		}
		
		System.out.println("\n========================================\n");
		//���ǽ� �ֱ��ڷ�
		for (int i=0; i< lectureList.size(); i++) {
			eclassRoomConnect(lectureCodeList.get(i));
			
			Document eclassDataPage = getPageDocument("http://eclass2.hufs.ac.kr:8181/ilos/st/course/lecture_material_list_form.acl");
			
			//�����ڷ�
			System.out.println("*** [" + lectureList.get(i) + "] �����ڷ� �ֱ� �Խù� ***");
			if(!eclassDataPage.select("div[class=top_div] span").isEmpty()) {
				Elements dataTitles = eclassDataPage.select("div[class=top_div] span");
				System.out.println(dataTitles.get(0).text());
			}
			else {
				System.out.println("===> �ֱ��ڷᰡ �����ϴ�..");
			}
			System.out.println();
		}
		
		//���̹����� �⼮���� �˷��ֱ� (���� ��)
		for(int i=0; i<lectureList.size(); i++){
		eclassRoomConnect(lectureCodeList.get(i));
		Document CheckCyberGang2 = getPageDocument("http://eclass2.hufs.ac.kr:8181/ilos/st/course/submain_form.acl");
		Elements cyberCheck = CheckCyberGang2.select("dd style[padding: 3px 10px; "
				+ "overflow: hidden; background-color:  #F39814; color: #fff;] div[style]");
		
 		System.out.println(cyberCheck.toString());
 		
		
		}
	}
	
	private static Document getPageDocument(String url) {
		Document sampleDoc = null;
		try {
			sampleDoc = Jsoup.connect(url)
					.userAgent(userAgent)
					.header("Referer", "http://eclass2.hufs.ac.kr:8181/ilos/main/main_form.acl")
					.header("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
					.header("Content-Type", "text/html; charset=utf-8")
					.header("Accept-Encoding", "gzip, deflate")
					.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
					.cookies(loginCookie) // ������ ���� '�α��� ��' ��Ű
					.method(Connection.Method.GET)
					.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sampleDoc;
	}
	
	/*
	 * E-CLASS connect GET
	 * http://eclass2.hufs.ac.kr:8181/ilos/st/course/eclass_room2.acl  ==> POST(�м���ȣ�ڵ带 �ѱ��)
	 * http://eclass2.hufs.ac.kr:8181/ilos/st/course/submain_form.acl  ==> GET
	 * ��� ���ǽ� �������� "/ilos/st/course/submain_form.acl" �� ����
	 * ������ �������� ������ �̷������ �ϴµ� �̶� "/ilos/st/course/eclass_room2.acl" ��ũ�� ���� �м��ڵ带 ������ �����͸� �Բ� POST
	 * ���������� ������ �Ǹ� JSON ������ ���� Ȯ���� �� ����
	 * �� �� �ٽ� "/ilos/st/course/submain_form.acl" �� GET �ϸ� �ش� ���ǽ� �������� �ҷ� �� �� �ִ�.
	 */ 
	private static void eclassRoomConnect(String lecCode) {
		// ������ �� ������
		Map<String, String> data = new HashMap<String, String>();
		data.put("KJKEY", lecCode);
		data.put("returnURI", "/ilos/st/course/submain_form.acl");
		data.put("encoding", "utf-8");

    	// POST (JSON ����)
		// KJKEY=A20191U5510620101&returnURI=%252Filos%252Fst%252Fcourse%252Fsubmain_form.acl&encoding=utf-8
    	try {
			String jsoupStr = Jsoup.connect("http://eclass2.hufs.ac.kr:8181/ilos/st/course/eclass_room2.acl")
					.userAgent(userAgent)
					.timeout(3000)
					.ignoreContentType(true)
					.cookies(loginCookie) // ������ ���� '�α��� ��' ��Ű
					.data(data)
					.method(Connection.Method.POST)
					.execute().body();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	//System.out.println(jsoupStr);
	}
}
