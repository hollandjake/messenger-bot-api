package com.hollandjake.messengerBotAPI.message;

import com.google.errorprone.annotations.ForOverride;
import com.hollandjake.messengerBotAPI.util.Config;
import com.hollandjake.messengerBotAPI.util.DatabaseController;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseObject extends MessageObject {
	final Integer id;

	DatabaseObject(Integer id) {
		this.id = id;
	}

	@ForOverride
	static Object fromResultSet(Config config, DatabaseController db, ResultSet resultSet) throws SQLException {
		throw new IllegalArgumentException("Subclass did not declare an overridden fromResultSet() method.");
	}

	public Integer getId() {
		return id;
	}
}
