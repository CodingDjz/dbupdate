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
	 * 解析ExportSQL.sql文件
	 */
	public void parseExportSQL() {
		try {

			Table table = null;
			while ((thisLine = br.readLine()) != null) {
				fieldCount++;
				String trimLine = thisLine.trim();
				writeTitleInfo();
				// 设置表名
				if (trimLine.startsWith("CREATE TABLE")) {
					fieldCount = 0;
					thisTableName = setTableName().toLowerCase();
					table = tablesMap.get(thisTableName);
				}
				// 表不需要修改
				if (table == null) {
					if (trimLine.contains(thisTableName)
							&& trimLine.startsWith("INSERT INTO ")) {
						pw.println(thisLine);
					}
					// 表需要修改
				} else {
					// 定位修改项
					if (trimLine.startsWith("`")) {
						processInfo(table);
						// 处理数据
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
	 * 写入sql文件头信息，一些配置信息
	 */
	private void writeTitleInfo() {
		if ("tableNameTitle".equals(thisTableName)) {
			if (!thisLine.contains("CREATE TABLE"))
				pw.println(thisLine);
		}
	}

	/**
	 * 设置表名
	 */
	private String setTableName() {
		return thisLine.substring(thisLine.indexOf("`") + 1,
				thisLine.lastIndexOf("`"));

	}

	/**
	 * 获得数据数组
	 * 
	 * @return 数组
	 */
	private String[] getDataArray() {
		String dataStr = thisLine.substring(thisLine.indexOf("S (") + 3,
				thisLine.indexOf(");"));
		return dataStr.split(",");

	}

	/**
	 * 获得字段名数组
	 *
	 */
	public String[] getFieldNameArray() {
		System.out.println("正在处理表：" + thisTableName);
		String fieldNameStr = thisLine.substring(thisLine.indexOf("` (") + 3,
				thisLine.indexOf(") V"));
		return fieldNameStr.split(",");
	}

	/**
	 * 根据表结构进行配置
	 * 
	 * @param table
	 */
	private void processInfo(Table table) {
		String thisFieldName = thisLine.substring(thisLine.indexOf("`") + 1,
				thisLine.lastIndexOf("`")).toLowerCase();
		// 当前字段信息(此行)
		FieldInfo fieldInfo = table.getField(thisFieldName);
		// 字段已存在 需要修改，增加字段坐标
		if (fieldInfo != null) {
			fieldInfo.setIndex(String.valueOf(fieldCount));
		}
	}

	/**
	 * 处理删改数据数组
	 * 
	 * @param table
	 * @return
	 */
	private HashMap<String, List<String>> processDataArray(Table table) {
		HashMap<String, List<String>> listsMap = new HashMap<String, List<String>>();
		String[] dataArray = getDataArray();
		String[] fieldNameArray = getFieldNameArray();
		// 此表所有需要修改的字段
		HashMap<String, FieldInfo> infoMap = table.getFiledsMap();
		for (Map.Entry<String, FieldInfo> entry : infoMap.entrySet()) {
			FieldInfo fieldInfo = entry.getValue();
			String operation = fieldInfo.getOperation();
			//增加操作则跳过 因为此处只修改已有字段
			if (operation.equalsIgnoreCase("add"))
				continue;
			String fieldName = fieldInfo.getFieldName();
			String newName = fieldInfo.getNewName();
			String index = fieldInfo.getIndex();
			int dataindex = Integer.parseInt(index) - 1;
			String thisData = dataArray[dataindex];
			// 字段名是否修改
			fieldName = newName == null ? fieldName : newName;
			// 进行修改
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
	 * 处理add数据
	 * 
	 * @param dataList
	 * @param table
	 * @return
	 * @throws IOException
	 */
	private String processInsertList(List<String> dataList,
			List<String> fieldNameList, Table table) throws IOException {
		HashMap<String, FieldInfo> infoMap = table.getFiledsMap();
		// 增加字段操作方法
		for (Map.Entry<String, FieldInfo> entry : infoMap.entrySet()) {
			FieldInfo fieldInfo = entry.getValue();
			String operation = fieldInfo.getOperation();
			String value = fieldInfo.getValue();
			String addValue = value == null ? "''" : value;
			// 增加
			if (operation.equalsIgnoreCase("add")) {
				String fieldName = fieldInfo.getFieldName();
				String nameData = null;
				/**
				 * 此处特殊处理增加数据的值
				 * 自行修改
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
		// 先add在设值
		return getModifyLine(dataList, fieldNameList);
	}

	/**
	 * 获得修改后的输出行
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
