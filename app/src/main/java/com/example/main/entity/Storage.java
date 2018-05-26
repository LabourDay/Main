package com.example.main.entity;

public class Storage {
	/**
	 * s_id            出入库id
	 * g_id            商品id
	 * g_name          商品名
	 * v_type          类型（进货/出货）
	 * v_amount       （进货/出货）数量
	 * v_time         （进货/出货）时间
	 * v_state         状态（是否可用）
	 */
	private String s_id;
	private String g_id;
	private String g_name;
	private String v_type;
	private int    v_amount;
	private String v_time;
	private String v_state;
	public String getS_id() {
		return s_id;
	}
	public void setS_id(String s_id) {
		this.s_id = s_id;
	}
	public String getG_id() {
		return g_id;
	}
	public void setG_id(String g_id) {
		this.g_id = g_id;
	}
	public String getG_name() {
		return g_name;
	}
	public void setG_name(String g_name) {
		this.g_name = g_name;
	}
	public String getV_type() {
		return v_type;
	}
	public void setV_type(String v_type) {
		this.v_type = v_type;
	}
	public int getV_amount() {
		return v_amount;
	}
	public void setV_amount(int v_amount) {
		this.v_amount = v_amount;
	}
	public String getV_time() {
		return v_time;
	}
	public void setV_time(String v_time) {
		this.v_time = v_time;
	}
	public String getV_state() {
		return v_state;
	}
	public void setV_state(String v_state) {
		this.v_state = v_state;
	}




}
