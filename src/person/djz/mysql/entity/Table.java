package person.djz.mysql.entity;

import java.util.HashMap;

public class Table {

	private HashMap<String, FieldInfo> filedsMap;
	
	public Table(){
		filedsMap = new HashMap<String, FieldInfo>();
	}
	public Table(HashMap<String, FieldInfo> FieldInfoMap){
		this.filedsMap = FieldInfoMap;
	}

	public FieldInfo getField(String fieldName) {
		return filedsMap.get(fieldName);
	}

	public void setField(String fieldName, FieldInfo fileldInfo) {
		filedsMap.put(fieldName, fileldInfo);
	}

	public HashMap<String, FieldInfo> getFiledsMap() {
		return filedsMap;
	}

	public void setFiledsMap(HashMap<String, FieldInfo> filedsMap) {
		this.filedsMap = filedsMap;
	}

}
