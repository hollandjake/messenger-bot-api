package com.hollandjake.messengerBotAPI.message;

import com.hollandjake.messengerBotAPI.util.Config;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageThread {
	private final Integer id;
	private final String threadName;

	private MessageThread(Integer id, String threadName) {
		this.id = id;
		this.threadName = threadName;
	}

	public static MessageThread fromResultSet(Config config, ResultSet resultSet) {
		try {
			return new MessageThread(resultSet.getInt("thread_id"), resultSet.getString("thread_name"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getId() {
		return id;
	}

	public String getThreadName() {
		return threadName;
	}
}
