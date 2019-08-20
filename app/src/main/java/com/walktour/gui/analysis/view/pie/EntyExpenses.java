package com.walktour.gui.analysis.view.pie;

public class EntyExpenses {
	
	private String expensesMainType;
	
	private float expensesNum;
	
	private float expensesPersent = 0;
	
	public EntyExpenses(String expensesMainType, float expensesNum){
		this.expensesNum = expensesNum;
		this.expensesMainType = expensesMainType;
	}
	
	public float getExpensesPersent() {
		return expensesPersent;
	}

	public void setExpensesPersent(float expensesPersent) {
		this.expensesPersent = expensesPersent;
	}

	public String getExpensesMainType() {
		return expensesMainType;
	}

	public void setExpensesMainType(String expensesMainType) {
		this.expensesMainType = expensesMainType;
	}

	public float getExpensesNum() {
		return expensesNum;
	}

	public void setExpensesNum(float expensesNum) {
		this.expensesNum = expensesNum;
	}
	
}
