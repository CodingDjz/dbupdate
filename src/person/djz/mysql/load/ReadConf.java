package person.djz.mysql.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import person.djz.mysql.dao.Dao;
import person.djz.mysql.entity.FieldInfo;
import person.djz.mysql.entity.Table;

public class ReadConf {
	File ImportSQL;
	Dao dao;
	Connection conn;

	public ReadConf() {
		try {
			ImportSQL = Tools.getFileByJarPath("SQLFile" + File.separator
					+ "ImportSQL.sql");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ImportSQL.exists()) {
			ImportSQL.delete();
		}
		dao = new Dao();
		conn = dao.connOldDB();
		conn = dao.connNewDB();
	}

	public BufferedReader getBR() throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(Tools.getFileByJarPath("SQLFile"
						+ File.separator + "ExportSQL.sql"))));
		return br;
	}

//	/**
//	 * ���ĳ�����ݣ����в���
//	 *
//	 * @return
//	 */
//	public HashMap<String, String> getTableDataMap() {
//		HashMap<String, String> MoMap = dao.selectMO(conn);
//		return MoMap;
//	}


	public void daoClose() {
		dao.closeDB(conn);
	}

	/**
	 * ��������
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public PrintWriter getPW() throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter pw = new PrintWriter(ImportSQL, "utf8");
		return pw;
	}

	/**
	 * ��ɾ�������ļ�
	 */
	public List<String> getDeleteTables() {
		Properties alterProp = new Properties();
		List delTabsList = new ArrayList();
		try {
			alterProp.load(new FileInputStream(Tools.getFileByJarPath("Config"
					+ File.separator + "DELETE.properties")));
			String[] delTablesArr = alterProp.getProperty("delete").split(";");
			for (String delTable : delTablesArr)
				delTabsList.add(delTable);
		} catch (Exception e) {
			System.out.println("���������ļ�����");
			e.printStackTrace();
		}
		return delTabsList;
	}

	/**
	 * ���޸������ļ�
	 */
	public Element getXMLRoot() {
		Element root = null;
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(Tools.getFileByJarPath("Config"
					+ File.separator + "ALTER.xml"));
			root = doc.getRootElement();
		} catch (Exception e) {
			System.out.println("����XML�ļ�����!");
			e.printStackTrace();
		}
		return root;
	}

	/**
	 * ��ñ���
	 * 
	 * @return
	 */
	public HashMap<String, Table> getTablesMap() {
		HashMap<String, Table> tablesMap = new HashMap<String, Table>();
		Element root = getXMLRoot();
		Iterator<Element> tableIt = root.elementIterator();
		// ������
		while (tableIt.hasNext()) {
			Element tableEle = tableIt.next();
			// ��ñ���
			String tableName = tableEle.getName();
			Iterator<Element> fieldIt = tableEle.elementIterator();
			Table table = new Table();
			FieldInfo fieldInfo = null;
			// ���������ֶ�
			while (fieldIt.hasNext()) {
				Element fieldEle = fieldIt.next();
				// ����ֶ���
				String fieldName = fieldEle.getName();
				String operation = fieldEle.attributeValue("operation");
				String newName = fieldEle.attributeValue("newName");
				String value = fieldEle.attributeValue("value");
				// String index = operation.equals("add") ? "1" : "-1";
				// info����
				fieldInfo = new FieldInfo(fieldName, operation);
				//����������
				if (newName != null)
					fieldInfo.setNewName(newName);
				//����Ĭ��ֵ
				if (value != null)
					fieldInfo.setValue(value);
				table.setField(fieldName.toLowerCase(), fieldInfo);
			}
			tablesMap.put(tableName.toLowerCase(), table);
		}
		return tablesMap;
	}

	public void writePath(String path) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(
					Tools.getFileByJarPath("SetPath.bat"), true),true);
			pw.println(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
