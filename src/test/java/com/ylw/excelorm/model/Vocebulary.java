package com.ylw.excelorm.model;

import com.ylw.excelorm.annotation.Cell;
import com.ylw.excelorm.annotation.ErrorHandle;
import com.ylw.excelorm.annotation.Sheet;

@Sheet(order = 0, wrongIgnoreNum = 4)
public class Vocebulary {
    @Cell(0)
    private int id;
    @Cell(1)
    private String name;
    @Cell(2)
    private String grade;
    @Cell(3)
    private float weight;
    
   
    @ErrorHandle(0)
    public int handle_0(Object obj){
        return 116;
    }


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getGrade() {
		return grade;
	}


	public void setGrade(String grade) {
		this.grade = grade;
	}


	public float getWeight() {
		return weight;
	}


	public void setWeight(float weight) {
		this.weight = weight;
	}


	@Override
	public String toString() {
		return "Vocebulary [id=" + id + ", name=" + name + ", grade=" + grade + ", weight=" + weight + "]";
	}
    
	
	
    
}