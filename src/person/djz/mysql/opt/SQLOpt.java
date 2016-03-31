package person.djz.mysql.opt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import person.djz.mysql.entity.FieldInfo;
import person.djz.mysql.entity.Table;
import person.djz.mysql.load.ReadConf;

public class SQLOpt {
	private PrintWriter pw;
	private BufferedReader br;
	private String thisLine;
	private String thisTableName;
	private HashMap<String, Table> tablesMap;
	private int fieldCount = 0;

	public SQLOpt() {
		try {
			ReadConf rc = new ReadConf();
			pw = rc.getPW();
			br = rc.getBR();
			tablesMap = rc.getTablesMap();
//			moidMap = rc.getTableDataMap();
			rc.daoClose();
		} catch (IOException e) {
			e.printStackTrace();
		}
		thisTableName = "tableNameTitle";
	}

	/**
	 * ����ExportSQL.sql�ļ�
	 */
	public void parseExportSQL() {
		try {

			Table table = null;
			while ((thisLine = br.readLine()) != null) {
				fieldCount++;
				String trimLine = thisLine.trim();
				writeTitleInfo();
				// ���ñ���
				if (trimLine.startsWith("CREATE TABLE")) {
					fieldCount = 0;
					thisTableName = setTableName().toLowerCase();
					table = tablesMap.get(thisTableName);
				}
				// ����Ҫ�޸�
				if (table == null) {
					if (trimLine.contains(thisTableName)
							&& trimLine.startsWith("INSERT INTO ")) {
						pw.println(thisLine);
					}
					// ����Ҫ�޸�
				} else {
					// ��λ�޸���
					if (trimLine.startsWith("`")) {
						processInfo(table);
						// ��������
					} else if (trimLine.contains(thisTableName)
							&& trimLine.startsWith("INSERT INTO ")) {
						HashMap<String, List<String>> listsMap = processDataArray(table);
						thisLine = processInsertList(listsMap.get("dataList"),
								listsMap.get("fieldNameList"), table);
						pw.println(thisLine);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pw != null)
					pw.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * д��sql�ļ�ͷ��Ϣ��һЩ������Ϣ
	 */
	private void writeTitleInfo() {
		if ("tableNameTitle".equals(thisTableName)) {
			if (!thisLine.contains("CREATE TABLE"))
				pw.println(thisLine);
		}
	}

	/**
	 * ���ñ���
	 */
	private String setTableName() {
		return thisLine.substring(thisLine.indexOf("`") + 1,
				thisLine.lastIndexOf("`"));

	}

	/**
	 * �����������
	 * 
	 * @return ����
	 */
	private String[] getDataArray() {
		String dataStr = thisLine.substring(thisLine.indexOf("S (") + 3,
				thisLine.indexOf(");"));
		return dataStr.split(",");

	}

	/**
	 * ����ֶ�������
	 *
	 */
	public String[] getFieldNameArray() {
		System.out.println("���ڴ����" + thisTableName);
		String fieldNameStr = thisLine.substring(thisLine.indexOf("` (") + 3,
				thisLine.indexOf(") V"));
		return fieldNameStr.split(",");
	}

	/**
	 * ���ݱ�ṹ��������
	 * 
	 * @param table
	 */
	private void processInfo(Table table) {
		String thisFieldName = thisLine.substring(thisLine.indexOf("`") + 1,
				thisLine.lastIndexOf("`")).toLowerCase();
		// ��ǰ�ֶ���Ϣ(����)
		FieldInfo fieldInfo = table.getField(thisFieldName);
		// �ֶ��Ѵ��� ��Ҫ�޸ģ������ֶ�����
		if (fieldInfo != null) {
			fieldInfo.setIndex(String.valueOf(fieldCount));
		}
	}

	/**
	 * ����ɾ����������
	 * 
	 * @param table
	 * @return
	 */
	private HashMap<String, List<String>> processDataArray(Table table) {
		HashMap<String, List<String>> listsMap = new HashMap<String, List<String>>();
		String[] dataArray = getDataArray();
		String[] fieldNameArray = getFieldNameArray();
		// �˱�������Ҫ�޸ĵ��ֶ�
		HashMap<String, FieldInfo> infoMap = table.getFiledsMap();
		for (Map.Entry<String, FieldInfo> entry : infoMap.entrySet()) {
			FieldInfo fieldInfo = entry.getValue();
			String operation = fieldInfo.getOperation();
			//���Ӳ��������� ��Ϊ�˴�ֻ�޸������ֶ�
			if (operation.equalsIgnoreCase("add"))
				continue;
			String fieldName = fieldInfo.getFieldName();
			String newName = fieldInfo.getNewName();
			String index = fieldInfo.getIndex();
			int dataindex = Integer.parseInt(index) - 1;
			String thisData = dataArray[dataindex];
			// �ֶ����Ƿ��޸�
			fieldName = newName == null ? fieldName : newName;
			// �����޸�
			switch (operation) {
			case "delete":
				dataArray[dataindex] = null;
				fieldNameArray[dataindex] = null;
				break;
			case "str-int":
				dataArray[dataindex] = thisData.replace("'", "");
				fieldNameArray[dataindex] = "`" + fieldName + "`";
				break;
			case "bool-bit":
				switch (thisData) {
				case "'true'":
					dataArray[dataindex] = "1";
					break;
				case "'false'":
					dataArray[dataindex] = "0";
				}
				fieldNameArray[dataindex] = "`" + fieldName + "`";
				break;
			case "modify-name":
				fieldNameArray[dataindex] = "`" + fieldName + "`";
			default:
				break;
			}
		}
		List<String> dataList = new ArrayList<String>();
		List<String> fieldNameList = new ArrayList<String>();
		for (String data : dataArray) {
			if (data != null)
				dataList.add(data);
		}
		for (String fieldName : fieldNameArray) {
			if (fieldName != null) {
				fieldNameList.add(fieldName);
			}
		}

		listsMap.put("dataList", dataList);
		listsMap.put("fieldNameList", fieldNameList);
		return listsMap;
	}

	/**
	 * ����add����
	 * 
	 * @param dataList
	 * @param table
	 * @return
	 * @throws IOException
	 */
	private String processInsertList(List<String> dataList,
			List<String> fieldNameList, Table table) throws IOException {
		HashMap<String, FieldInfo> infoMap = table.getFiledsMap();
		// �����ֶβ�������
		for (Map.Entry<String, FieldInfo> entry : infoMap.entrySet()) {
			FieldInfo fieldInfo = entry.getValue();
			String operation = fieldInfo.getOperation();
			String value = fieldInfo.getValue();
			String addValue = value == null ? "''" : value;
			// ����
			if (operation.equalsIgnoreCase("add")) {
				String fieldName = fieldInfo.getFieldName();
				String nameData = null;
				/**
				 * �˴����⴦���������ݵ�ֵ
				 * �����޸�
				 */
				switch (fieldName.toLowerCase()) {
				case "value":
					/*BaLabalabala*/
					dataList.add("");
				}
				dataList.add(addValue);
				fieldNameList.add("`" + fieldName + "`");
			}
		}
		// ��add����ֵ
		return getModifyLine(dataList, fieldNameList);
	}

	/**
	 * ����޸ĺ�������
	 * 
	 * @param dataList
	 * @return
	 */
	private String getModifyLine(List<String> dataList,
			List<String> fieldNameList) {

		StringBuilder line = new StringBuilder(thisLine.substring(0,
				thisLine.indexOf("(") + 1));

		for (String fieldName : fieldNameList) {
			line = line.append(fieldName).append(",");
		}
		line.replace(line.lastIndexOf(","), line.lastIndexOf(",") + 1, ")");
		line = line.append(" VALUES (");
		for (String data : dataList) {
			line = line.append(data).append(",");
		}
		// System.out.println("preLine:" + line);
		line.replace(line.lastIndexOf(","), line.lastIndexOf(",") + 1, ");");
		// System.out.println("aftLine:" + line);
		return line.toString();
	}
}
