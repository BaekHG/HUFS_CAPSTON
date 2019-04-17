import java.io.File;
import java.io.FileNotFoundException;
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
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Class_classify {
	// Windows, Whale의 User Agent.
	static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Whale/1.4.64.6 Safari/537.36";

	public static void main(String[] args) throws IOException, Exception {
//		// 자바스크립트 function 호출을 위해 Nashorn JavaScript 엔진 사용
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval(new FileReader("./src/sha512.js"));

		// cast the script engine to an invocable instance
		Invocable invocable = (Invocable) engine;
		 Object result = invocable.invokeFunction("SHA512", "sdfdfsf");
		String lang= "";
		String year= "";
		String session="";
		String section="";
		String camput="";
		String gubun="";
		String strict="";
		String compt="";
		String classURL= "https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp"
				+ "?tab_lang="+ lang +"&ad_ledg_year="+ year +"&ad_ledg_sessn="+session
				+ "&ad_org_sect=" +section+"&campus_sect="+ camput+"&gubun="+gubun
				+"&ag_crs_strct_cd="+strict
				+ "&ag_compt_fld_cd="+compt;
		
		//수강과목페이지
    	Connection.Response response1 = Jsoup.connect(classURL)
				.userAgent(userAgent)
				.timeout(3000)
				.header("Origin", "https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp")
				.header("Referer", "http://eclass2.hufs.ac.kr:8181/ilos/main/member/login_form.acl")
				.header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
				.header("Content-Type", "text/html;charset=UTF-8")
				.header("Connection", "keep-alive")
				.header("Accept-Encoding", "gzip, deflate , br")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.method(Connection.Method.POST)
				.execute();
    	System.out.println(response1.statusMessage());
//    	
    	Document mainPage=getPageDocument("https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp");
//    	System.out.println(mainPage.toString());
 
    	Elements e1 = mainPage.select("select[name=ag_crs_strct_cd]");

    	List<String> classList= new ArrayList<String>(); //Area 이름
 //   	System.out.println(e1);
    	e1.get(0).attr("value");
    	String a1 = e1.get(0).text().substring(8);
    	System.out.println(a1);

    	
    	
    	}

	private static Document getPageDocument(String url) {
		Document sampleDoc = null;
		try {
			sampleDoc = Jsoup.connect(url)
					.userAgent(userAgent)
					.timeout(3000)
					.header("Origin", "https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp")
					.header("Referer", "http://eclass2.hufs.ac.kr:8181/ilos/main/member/login_form.acl")
					.header("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
					.header("Content-Type", "text/html;charset=UTF-8")
					.header("Connection", "keep-alive")
					.header("Accept-Encoding", "gzip, deflate , br")
					.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
					.method(Connection.Method.GET)
					.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sampleDoc;
	}

	private static void classConnect(String lang,String year,String session,String section,String camput, String gubun,String strict, String compt) {
		Map<String,String> data = new HashMap<String,String>();
		data.put("tag_lang", lang);
		data.put("ag_ledg_year", year);
		data.put("ad_ledg_sessn", session);
		data.put("ag_org_sect", section);
		data.put("gubun", gubun);
		data.put("ag_crs_strct_cd", strict);
		data.put("ag_compt_ld_cd", compt);
		
		//POST(JSON 응답)
		try {
			String jsoupStr = Jsoup.connect("https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp")
					.userAgent(userAgent)
					.timeout(3000)
					.ignoreContentType(true)
					.data(data)
					.method(Connection.Method.POST)
					.execute().body();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
