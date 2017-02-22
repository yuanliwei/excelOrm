package com.ylw.excelorm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldObj {
	private int index;
	private String name;
	private Method errorHandle;
	private Field field;
	private Method setMethod;

	public FieldObj(int index, Field field, Method setMethod, String name) {
		this.index = index;
		this.name = name;
		this.field = field;
		this.setMethod = setMethod;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setErrorHandle(Method method) {
		this.errorHandle = method;
	}

	public Method getErrorHandle() {
		return errorHandle;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Method getSetMethod() {
		return setMethod;
	}

	public void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}

}
