package com.acxiom.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.acxiom.bean.LogModel;

@Controller
@RequestMapping("/log")
@CrossOrigin
public class LogController {
	@GetMapping("/getSize")
	@ResponseBody
	public long getFileSize() {
		File logFile = new File("nohup.out");
		return logFile.length();
	}
	@GetMapping("/")
	@ResponseBody
	public LogModel getLogs(Long lastTimeFileSize) {
		LogModel model = new LogModel();
		File logFile = new File("nohup.out");
		RandomAccessFile randomFile = null;
		try {
			randomFile = new RandomAccessFile(logFile, "rw");
			// 获得变化部分的
			randomFile.seek(lastTimeFileSize);
			StringBuffer log = new StringBuffer();
			String tmp = "";
			while ((tmp = randomFile.readLine()) != null) {
				log.append(tmp);
				log.append("<br />");
			}
			long fileSize = randomFile.length();
			model.setFileSize(fileSize);
			model.setMsg(log.toString());
			return model;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				randomFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
