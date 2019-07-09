package com.hollandjake.messenger_bot_api.message;

import com.google.errorprone.annotations.ForOverride;
import com.hollandjake.messenger_bot_api.util.Config;
import com.hollandjake.messenger_bot_api.util.DatabaseController;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseObject extends MessageObject {
	private final Integer id;

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
