package com.mapper;

import com.alibaba.fastjson.JSON;
import com.enums.BizTypeEnum;
import com.enums.SyncDataStatusEnum;
import com.mapper.ding.OpenSyncBizDataMapper;
import com.mapper.ding.OpenSyncBizDataMediumMapper;
import com.model.OpenSyncBizDataDO;
import com.service.SystemConfigServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenSyncBizDataBaseMapperTest {

	@Resource
	private OpenSyncBizDataMapper openSyncBizDataMapper;

	@Resource
	private OpenSyncBizDataMediumMapper openSyncBizDataMediumMapper;

	@Resource
	private SystemConfigServiceImpl systemConfigService;
	@Test
	public void testGetOpenSyncBizData() {
		String subscribeId = systemConfigService.getSuiteId()+"_0";
		String corpId = "ding9f50b15bccd16741";
		Integer bizType = BizTypeEnum.ORG_AUTH.getValue();
		String bizId = String.valueOf(systemConfigService.getSuiteId());

		OpenSyncBizDataDO openSyncBizDataDO = openSyncBizDataMapper.getOpenSyncBizData(subscribeId,corpId,bizType,bizId);
		System.out.println(JSON.toJSONString(openSyncBizDataDO));

		bizType = BizTypeEnum.ORG_USER.getValue();
		bizId = "sss";
		openSyncBizDataDO = openSyncBizDataMediumMapper.getOpenSyncBizData(subscribeId,corpId,bizType,bizId);
		System.out.println(JSON.toJSONString(openSyncBizDataDO));

	}


	@Test
	public void testOpenSyncBizDataListExcludeCorpIdByStatus() {
		String subscribeId = systemConfigService.getSuiteId()+"_0";
		List<String> excludeCorpIdList = new ArrayList<>();
		excludeCorpIdList.add("aa");
		excludeCorpIdList.add("bb");

		List<OpenSyncBizDataDO> openSyncBizDataDOList = openSyncBizDataMapper.getOpenSyncBizDataListExcludeCorpIdByStatus(subscribeId,excludeCorpIdList, SyncDataStatusEnum.DEAL_WAIT.getValue(),10);
		System.out.println(JSON.toJSONString(openSyncBizDataDOList));

		openSyncBizDataDOList = openSyncBizDataMediumMapper.getOpenSyncBizDataListExcludeCorpIdByStatus(subscribeId,excludeCorpIdList, SyncDataStatusEnum.DEAL_WAIT.getValue(),10);
		System.out.println(JSON.toJSONString(openSyncBizDataDOList));
		System.out.println(JSON.toJSONString(openSyncBizDataDOList));
	}


	@Test
	public void testUpdateOpenSyncBizDataStatus() {
		openSyncBizDataMapper.updateOpenSyncBizDataStatus(1L,SyncDataStatusEnum.DEAL_SUCCESS.getValue());
		openSyncBizDataMediumMapper.updateOpenSyncBizDataStatus(1L,SyncDataStatusEnum.DEAL_WAIT.getValue());

	}






}