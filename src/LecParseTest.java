import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



public class LecParseTest {

	static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Whale/1.4.64.6 Safari/537.36";
	
	static List<HashMap<String, String>> majorCodeList = null;
	static List<HashMap<String, String>> liberalCodeList = null;
	static List<HashMap<String, String>> majorLecList = null;
	static List<HashMap<String, String>> liberalLecList = null;

	/*
	ag_ledg_year(����) : 2019
	ag_ledg_sessn(�б�) : 1(1�б�), 2(��������), 3(2�б�), 4(�ܿ����)
	ag_org_sect(�Ҽӱ���) : A(�к�), B(���п�) ... 
	campus_sect(ķ�۽� ����) : H1(����), H2(�۷ι�)
	gubun(����/���� ���� ����) : 1(���������� ������ ���), 2(���翵���� ������ ���)
	ag_crs_strct_cd: AQR02_H2
	ag_compt_fld_cd: 303_H2
	*/
	static String param_year;
	static String param_session;
	static String param_orgSect;
	static String param_camSect;
	static String param_gubun;
	static String param_MajorCode;
	static String param_LiberalCode;
	
	public static void main(String[] args) {

		/* ��Ÿ���� ���� �ʱ�ȭ */
		param_year = "2019";
		param_session = "1";
		param_orgSect = "A";
		param_camSect = "H2";
		param_gubun = "1";
		param_MajorCode = "AQR02_H2";
		param_LiberalCode = "303_H2";
		
		// ��� ���� �� ���� ���� �ڵ带 ������ HashMap ArrayList ����
		majorCodeList = new ArrayList<HashMap<String, String>>();
		liberalCodeList = new ArrayList<HashMap<String, String>>();
		// ��� ���� �� ���� ���� ������ ������ HashMap ArrayList ����
		majorLecList = new ArrayList<HashMap<String, String>>();
		liberalLecList = new ArrayList<HashMap<String, String>>();
		
		/* Start Parsing task */
		startMajorLecParsing();
		startLiberalLecParsing();

		/* Text file out �׽�Ʈ */
//		saveMajorParseResultAsTxt();
//		saveLiberalParseResultAsTxt();

	}

	private static void startLiberalLecParsing() {
		Document lecInitDoc = getPageDoc("https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp?tab_lang=K&ag_ledg_year=2019&ag_ledg_sessn=1&ag_org_sect=A&campus_sect=H2");
		
		/* 1. ���� ���� �ڵ� �Ľ� (�۷ι� ����) */	
		Elements liberalCodeElements = lecInitDoc.select("select[name=ag_compt_fld_cd] option");
		
		for(int i = 0; i < liberalCodeElements.size(); i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("code", liberalCodeElements.get(i).attr("value"));
			temp.put("title", liberalCodeElements.get(i).text());
			//System.out.println(temp.get("code") + "\t" + temp.get("title"));
			liberalCodeList.add(temp);
		}
		
		/* 2. ���� ���� ���� object �Ľ� (�۷ι� ����) */
		// test : ��� ���� ���� code�� ������ ���� �������� GET�ϰ� �� ���� object�� �Ľ��Ͽ� liberalLectList�� ���� �Ѵ�.
		System.out.print("Parsing liberal lectures now...");
		for(int i = 0; i < liberalCodeList.size(); i++) {
			Document liberalLecDoc = getLiberalLecDoc(param_year, param_session, param_orgSect, param_camSect, 
					liberalCodeList.get(i).get("code"));
			Elements liberalLecElements = liberalLecDoc.select("div[id=premier1] tbody tr");
			for(int j = 1; j < liberalLecElements.size(); j++) {
				// ���� ����
				Elements liberalLecTdElements = liberalLecElements.get(j).select("td");
				HashMap<String, String> temp = new HashMap<String, String>();
				//temp.put("raw", liberalLecElements.get(j).text());
				temp.put("gubun", liberalCodeList.get(i).get("title"));
				temp.put("area", liberalLecTdElements.get(1).text());
				temp.put("year", liberalLecTdElements.get(2).text());
				temp.put("code", liberalLecTdElements.get(3).toString());
				temp.put("title", liberalLecTdElements.get(4).text());
				temp.put("prof", liberalLecTdElements.get(11).text());
				temp.put("credit", liberalLecTdElements.get(12).text());
				temp.put("time", liberalLecTdElements.get(13).text());
				temp.put("sched", liberalLecTdElements.get(14).text());
				temp.put("numpeople", liberalLecTdElements.get(15).text());
				temp.put("note", liberalLecTdElements.get(16).text());
				System.out.println(temp.get("numpeople")); // ��� test
				liberalLecList.add(temp);
			}
//			System.out.print(".");
		}
		System.out.println("Done!");
		System.out.println("liberalLecList size (��� ���� ���� ��) : " + liberalLecList.size());
	}

	private static void startMajorLecParsing() {
		Document lecInitDoc = getPageDoc("https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp?tab_lang=K&ag_ledg_year=2019&ag_ledg_sessn=1&ag_org_sect=A&campus_sect=H2");
		
		/* 1. ���� ���� �ڵ� �Ľ� (�۷ι� ����) */
		Elements majorCodeElements = lecInitDoc.select("select[name=ag_crs_strct_cd] option");
		
		for(int i = 0; i < majorCodeElements.size(); i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp.put("code", majorCodeElements.get(i).attr("value"));
			temp.put("title", majorCodeElements.get(i).text().substring(8));
			System.out.println(temp.get("code") + "\t" + temp.get("title"));
			majorCodeList.add(temp);
		}
		
		/* 2. ���� ���� ���� object �Ľ� (�۷ι� ����) */
		// test : ��� ���� ���� code�� ������ ���� �������� GET�ϰ� �� ���� object�� �Ľ��Ͽ� majorLectList�� ���� �Ѵ�.
		System.out.print("Parsing major lectures now...");
		for(int i = 0; i < majorCodeList.size(); i++) {
			Document majorLecDoc = getMajorLecDoc(param_year, param_session, param_orgSect, param_camSect, 
					majorCodeList.get(i).get("code"));
			Elements majorLecElements = majorLecDoc.select("div[id=premier1] tbody tr");
			for(int j = 1; j < majorLecElements.size(); j++) {
				// ���� ����
				Elements majorLecTdElements = majorLecElements.get(j).select("td");
				HashMap<String, String> temp = new HashMap<String, String>();
				//temp.put("raw", majorLecElements.get(j).text());
				temp.put("gubun", majorCodeList.get(i).get("title"));
				temp.put("area", majorLecTdElements.get(1).text());
				temp.put("year", majorLecTdElements.get(2).text());
				temp.put("code", majorLecTdElements.get(3).text());
				temp.put("title", majorLecTdElements.get(4).text());
				temp.put("prof", majorLecTdElements.get(11).text());
				temp.put("credit", majorLecTdElements.get(12).text());
				temp.put("time", majorLecTdElements.get(13).text());
				temp.put("sched", majorLecTdElements.get(14).text());
				temp.put("numpeople", majorLecTdElements.get(15).text());
				temp.put("note", majorLecTdElements.get(16).text());
				//System.out.println(temp.get("raw")); // ��� test
				majorLecList.add(temp);
			}
//			System.out.println(majorLecList.get(1));
			System.out.print(".");
		}
		System.out.println("Done!");
		System.out.println("majorLecList size (��� ���� ���� ��) : " + majorLecList.size());
	}
	
	private static void saveLiberalParseResultAsTxt() {
		liberalLecList.clear();
		try {
			FileWriter fw = new FileWriter("./src/liberal_result.txt"); // �����ּ� ��� ����
			BufferedWriter bw = new BufferedWriter(fw);

			System.out.print("Saving the parsing result as a txt file...");
			for (int i = 0; i < liberalCodeList.size(); i++) {
				
				bw.write("========== " + liberalCodeList.get(i).get("title") + " ==========");
				bw.newLine(); // �ٹٲ�

				Document liberalLecDoc = getLiberalLecDoc(param_year, param_session, param_orgSect, param_camSect,
						liberalCodeList.get(i).get("code"));
				Elements liberalLecElements = liberalLecDoc.select("div[id=premier1] tbody tr");
				for (int j = 1; j < liberalLecElements.size(); j++) {
					// �ϴ��� ��°�� String ���·� ����, ���� ������ ���Ŀ�
					HashMap<String, String> temp = new HashMap<String, String>();
					temp.put("raw", liberalLecElements.get(j).text());
					bw.write(temp.get("raw"));
					bw.newLine(); // �ٹٲ�
					liberalLecList.add(temp);
				}
				System.out.print(".");
				bw.newLine();
			}
			
			System.out.println("Done!");

			System.out.println(liberalLecList.size() + " liberal lectures has been parsed successfully.");
			bw.write(liberalLecList.size() + " liberal lectures has been parsed successfully.");
			bw.newLine(); // �ٹٲ�

			bw.close();
		} catch (IOException e) {
			System.err.println(e); // ������ �ִٸ� �޽��� ���
			System.exit(1);
		}
	}

	private static void saveMajorParseResultAsTxt() {
		majorLecList.clear();
		try {
			FileWriter fw = new FileWriter("./src/major_result.txt"); // �����ּ� ��� ����
			BufferedWriter bw = new BufferedWriter(fw);

			System.out.print("Saving the parsing result as a txt file...");
			for (int i = 0; i < majorCodeList.size(); i++) {
				
				bw.write("========== " + majorCodeList.get(i).get("title") + " ==========");
				bw.newLine(); // �ٹٲ�

				Document majorLecDoc = getMajorLecDoc(param_year, param_session, param_orgSect, param_camSect,
						majorCodeList.get(i).get("code"));
				Elements majorLecElements = majorLecDoc.select("div[id=premier1] tbody tr");
				for (int j = 1; j < majorLecElements.size(); j++) {
					// �ϴ��� ��°�� String ���·� ����, ���� ������ ���Ŀ�
					HashMap<String, String> temp = new HashMap<String, String>();
					temp.put("raw", majorLecElements.get(j).text());
					bw.write(temp.get("raw"));
					bw.newLine(); // �ٹٲ�
					majorLecList.add(temp);
				}
				System.out.print(".");
				bw.newLine();
			}
			
			System.out.println("Done!");

			System.out.println(majorLecList.size() + " major lectures has been parsed successfully.");
			bw.write(majorLecList.size() + " major lectures has been parsed successfully.");
			bw.newLine(); // �ٹٲ�

			bw.close();
		} catch (IOException e) {
			System.err.println(e); // ������ �ִٸ� �޽��� ���
			System.exit(1);
		}
		
	}

	private static Document getPageDoc(String url) {
		Document sampleDoc = null;
		try {
			sampleDoc = Jsoup.connect(url)
					.userAgent(userAgent)
					.method(Connection.Method.GET)
					.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sampleDoc;
	}
	
	private static Document getMajorLecDoc(String year, String session, String orgSect, String camSect, String majorCode) {
		Document sampleDoc = null;
		// �������� ������ GET
		String majorLecURL = "https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp" + 
				"?tab_lang=K" +
				"&ag_ledg_year=" + year + 
				"&ag_ledg_sessn=" + session +
				"&ag_org_sect=" + orgSect +
				"&campus_sect=" + camSect +
				"&gubun=1" +
				"&ag_crs_strct_cd=" + majorCode;
		//System.out.println(majorLecURL);
		try {
			sampleDoc = Jsoup.connect(majorLecURL)
					.userAgent(userAgent)
					.method(Connection.Method.GET)
					.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sampleDoc;
	}
	
	private static Document getLiberalLecDoc(String year, String session, String orgSect, String camSect, String liberalCode) {
		Document sampleDoc = null;
		// �������� ������ GET
		String liberalLecURL = "https://wis.hufs.ac.kr/src08/jsp/lecture/LECTURE2020L.jsp" + 
				"?tab_lang=K" +
				"&ag_ledg_year=" + year + 
				"&ag_ledg_sessn=" + session +
				"&ag_org_sect=" + orgSect +
				"&campus_sect=" + camSect +
				"&gubun=2" +
				"&ag_compt_fld_cd=" + liberalCode;
		try {
			sampleDoc = Jsoup.connect(liberalLecURL)
					.userAgent(userAgent)
					.method(Connection.Method.GET)
					.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sampleDoc;
	}
}