/*
 *    Copyright (c) 2018-2025, Eddid All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: Eddid (River.lu@newtype.io)
 */

package com.lotstock.eddid.codegen.service.impl;

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lotstock.eddid.codegen.service.SysGeneratorService;
import com.lotstock.eddid.codegen.entity.GenConfig;
import com.lotstock.eddid.codegen.mapper.SysGeneratorMapper;
import com.lotstock.eddid.codegen.util.GenUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author Eddid
 * @date 2019-05-20
 */
@Service
public class SysGeneratorServiceImpl implements SysGeneratorService {
	@Autowired
	private SysGeneratorMapper sysGeneratorMapper;


	/**
	 * 批量生成代码
	 *
	 * @param genConfigs 生成配置
	 * @return
	 */
	@Override
	public byte[] generatorCode(List<GenConfig> genConfigs) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		for(GenConfig genConfig: genConfigs) {
			//查询表信息
			Map<String, String> table = queryTable(genConfig.getTableName());
			//查询列信息
			List<Map<String, String>> columns = queryColumns(genConfig.getTableName());
			//生成代码
			GenUtils.generatorCode(genConfig, table, columns, zip);
		}
		IoUtil.close(zip);
		return outputStream.toByteArray();
	}

	/**
	 * 批量模糊生成代码
	 *
	 * @param genConfig 生成配置
	 * @return
	 */
	@Override
	public byte[] generatorCodeLike(GenConfig genConfig) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		List<Map<String, String>> tables = queryList(genConfig.getTableName());
		for(Map<String, String> table: tables) {
			genConfig.setTableName(table.get("tableName"));
			//查询列信息
			List<Map<String, String>> columns = queryColumns(genConfig.getTableName());
			//生成代码
			GenUtils.generatorCode(genConfig, table, columns, zip);
		}
		IoUtil.close(zip);
		return outputStream.toByteArray();
	}

	private List<Map<String, String>> queryList(String tableName) {
		return sysGeneratorMapper.queryList(tableName);
	}

	private Map<String, String> queryTable(String tableName) {
		return sysGeneratorMapper.queryTable(tableName);
	}

	private List<Map<String, String>> queryColumns(String tableName) {
		return sysGeneratorMapper.queryColumns(tableName);
	}
}
