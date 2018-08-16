package com.model.biz;

import java.util.Date;

public class AuthedCorpDO {
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	/**
	 * 企业名称
	 */
	private String corpName;
	/**
	 * 企业ID
	 */
	private String corpId;
	/**
	 * 企业访问accessToken
	 */
	private String accessToken;
	/**
	 * 企业访问accessToken过期时间,绝对时间。单位毫秒
	 */
	private Long accessTokenExpire;
	/**
	 * 企业授权
	 */
	private String permanentCode;
	/**
	 * 授权应用套件KEY
	 */
	private String suiteKey;
	/**
	 * 企业开通的应用实例ID
	 */
	private Long agentId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getAccessTokenExpire() {
		return accessTokenExpire;
	}

	public void setAccessTokenExpire(Long accessTokenExpire) {
		this.accessTokenExpire = accessTokenExpire;
	}

	public String getPermanentCode() {
		return permanentCode;
	}

	public void setPermanentCode(String permanentCode) {
		this.permanentCode = permanentCode;
	}

	public String getSuiteKey() {
		return suiteKey;
	}

	public void setSuiteKey(String suiteKey) {
		this.suiteKey = suiteKey;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
}
