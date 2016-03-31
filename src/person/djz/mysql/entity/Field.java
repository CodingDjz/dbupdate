package person.djz.mysql.entity;


public class Field{

	private String fieldName;
	
	Field(String fieldName){		
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
}
