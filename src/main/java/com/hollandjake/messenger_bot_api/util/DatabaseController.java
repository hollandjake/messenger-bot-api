package com.hollandjake.messenger_bot_api.util;

import com.hollandjake.messenger_bot_api.API;
import com.hollandjake.messenger_bot_api.message.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing the connections to the database
 */
public class DatabaseController {

	private final Config config;
	private final API api;
	private final MessageThread thread;
	private Duration connectionTimeout;
	private LocalDateTime lastCheck;
	private Connection connection;
	/**
	 * Gets a {@link MessageThread} from the database
	 * If one doesnt exist it creates it
	 * <p>
	 * Params: <br>
	 * &emsp; threadName {@link String} The threads name<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; thread_id {@link Integer} Threads Identifier <br>
	 * &emsp; thread_name {@link String} Threads name<br>
	 */
	private CallableStatement GET_THREAD;
	/**
	 * Gets a {@link Human} from the database
	 * If one doesnt exist it creates it
	 * <p>
	 * Params: <br>
	 * &emsp; humanName {@link String} The Humans name<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; human_id {@link Integer} Human id<br>
	 * &emsp; name {@link String} Human name<br>
	 */
	private CallableStatement GET_HUMAN;
	/**
	 * Gets a {@link Human} from the database
	 * If one doesnt exist it creates it
	 * <p>
	 * Params: <br>
	 * &emsp; humanName {@link String} The Humans name<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; human_id {@link Integer} Human id<br>
	 * &emsp; name {@link String} Human name<br>
	 */
	private CallableStatement GET_HUMAN_WITH_NAME_LIKE;
	/**
	 * Saves a {@link Message} to the database
	 * <p>
	 * Params: <br>
	 * &emsp; threadId {@link Integer} The thread id<br>
	 * &emsp; senderId {@link String} The senders {@link Human} name<br>
	 * &emsp; date {@link LocalDateTime} The messages date<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; message_id {@link Integer} Messages Identifier<br>
	 * &emsp; date {@link LocalDateTime} Messages date<br>
	 * &emsp; human_id {@link Integer} Sender id<br>
	 * &emsp; name {@link String} Sender name<br>
	 */
	private CallableStatement SAVE_MESSAGE;

	//region Queries
	/**
	 * Gets a {@link Message} from the database
	 * <p>
	 * Params: <br>
	 * &emsp; threadId {@link Integer} The thread id<br>
	 * &emsp; messageId {@link Integer} The message id<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; message_id {@link Integer} Messages Identifier<br>
	 * &emsp; date {@link LocalDateTime} Messages date<br>
	 * &emsp; human_id {@link Integer} Sender id<br>
	 * &emsp; name {@link String} Sender name<br>
	 */
	private PreparedStatement GET_MESSAGE;
	/**
	 * Gets the latest {@link Message} from the database for a thread
	 * <p>
	 * Params: <br>
	 * &emsp; threadId {@link Integer} The thread id<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; message_id {@link Integer} Messages Identifier<br>
	 * &emsp; date {@link LocalDateTime} Messages date<br>
	 * &emsp; human_id {@link Integer} Sender id<br>
	 * &emsp; name {@link String} Sender name<br>
	 */
	private CallableStatement GET_LATEST_MESSAGE;
	/**
	 * Saves a {@link Image} to the database
	 * <p>
	 * Params: <br>
	 * &emsp; threadId {@link Integer} The threads id<br>
	 * &emsp; messageId {@link Integer} The message id<br>
	 * &emsp; imageData {@link BufferedImage} The image data<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; image_id {@link Integer} Images Identifier <br>
	 */
	private PreparedStatement SAVE_MESSAGE_IMAGE;
	/**
	 * Gets a {@link List<Image>} from a message
	 * <p>
	 * Params: <br>
	 * &emsp; threadId {@link Integer} The threads id<br>
	 * &emsp; messageId {@link Integer} The message id<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; image_id {@link Integer} Images Identifier <br>
	 * &emsp; data {@link BufferedImage} Images data<br>
	 */
	private PreparedStatement GET_MESSAGE_IMAGE;
	/**
	 * Saves a {@link Text} to the database
	 * <p>
	 * Params: <br>
	 * &emsp; threadId {@link Integer} The threads id<br>
	 * &emsp; messageId {@link Integer} The message id<br>
	 * &emsp; content {@link String} The text<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; text_id {@link Integer} Texts Identifier <br>
	 */
	private PreparedStatement SAVE_MESSAGE_TEXT;
	/**
	 * Gets a {@link List<Text>} from a message
	 * <p>
	 * Params: <br>
	 * &emsp; threadId {@link Integer} The threads id<br>
	 * &emsp; messageId {@link Integer} The message id<br>
	 * <p>
	 * Returns: <br>
	 * &emsp; text_id {@link Integer} Texts Identifier <br>
	 * &emsp; text {@link String} The content<br>
	 */
	private PreparedStatement GET_MESSAGE_TEXT;

	public DatabaseController(Config config) throws SQLException {
		this(config, null);
	}

	public DatabaseController(Config config, API api) throws SQLException {
		config.checkForProperties("db_url", "db_username", "db_password");
		this.api = api;
		this.config = config;
		if (config.hasProperty("db_connection_timeout")) {
			connectionTimeout = Duration.ofSeconds(Long.parseLong(config.getProperty("db_connection_timeout")));
		} else {
			connectionTimeout = (api != null ? api.getMessageTimeout() : Duration.ofMinutes(1));
		}
		openConnection();
		try {
			Runtime.getRuntime().addShutdownHook(new Thread(this::closeConnection));
		} catch (IllegalArgumentException ignore) {
		}
		this.thread = getThread(config.getProperty("thread_name"));
	}

	/**
	 * Creates a connection and handles any errors
	 */
	private void openConnection() {
		closeConnection();

		try {
			if (api != null && api.debugging()) {
				System.out.println("Connecting to Database");
			}

			connection = DriverManager.getConnection(
					config.getProperty("db_url") + "?" +
							"sessionVariables=wait_timeout=" + (connectionTimeout.getSeconds()) + "," +
							"character_set_client=utf8mb4,character_set_results=utf8mb4,character_set_connection=utf8mb4" +
							"&autoReconnect=true" +
							"&useCompression=true" +
							"&allowMultiQueries=true" +
							"&rewriteBatchedStatements=true",
					config.getProperty("db_username"),
					config.getProperty("db_password"));
			createQueries();
			lastCheck = LocalDateTime.now();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles closing the connection when its finished with or failed
	 */
	private void closeConnection() {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
					System.out.println("Connection closed");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//endregion

	/**
	 * checks the connections status to make sure a connection is always active
	 */
	public void checkConnection() {
		if (connection == null) {
			openConnection();
		} else {
			LocalDateTime now = LocalDateTime.now();
			if (now.isAfter(lastCheck.plus(connectionTimeout))) {
				try {
					PreparedStatement stmt = connection.prepareStatement("SELECT NOW()");
					stmt.execute();
				} catch (SQLException e) {
					openConnection();
				}
			}
		}
	}

	//region Connection

	private void createQueries() throws SQLException {

		//region Thread
		GET_THREAD = connection.prepareCall("{CALL GetThread(?)}");
		//endregion

		//region Human
		GET_HUMAN = connection.prepareCall("{CALL GetHuman(?)}");
		GET_HUMAN_WITH_NAME_LIKE = connection.prepareCall("{CALL GetHumanWithNameLike(?)}");
		//endregion

		//region Message
		SAVE_MESSAGE = connection.prepareCall("{CALL SaveMessage(?, ?, ?)}");

		GET_MESSAGE = connection.prepareStatement("" +
				"SELECT " +
				"   M.message_id," +
				"   date," +
				"   H.human_id," +
				"   name " +
				"FROM message M " +
				"JOIN human H on M.sender_id = H.human_id " +
				"WHERE thread_id = ? AND message_id = ? " +
				"LIMIT 1");

		GET_LATEST_MESSAGE = connection.prepareCall("{CALL GetLatestMessage(?)}");
		//endregion

		//region Text
		GET_MESSAGE_TEXT = connection.prepareStatement("" +
				"SELECT " +
				"   T.text_id," +
				"   text " +
				"FROM text T " +
				"JOIN message_text MT on T.text_id = MT.text_id " +
				"WHERE thread_id = ? AND message_id = ?");

		SAVE_MESSAGE_TEXT = connection.prepareStatement("SELECT SaveText(?, ?, ?)");
		//endregion

		//region Image
		GET_MESSAGE_IMAGE = connection.prepareStatement("" +
				"SELECT " +
				"   I.image_id," +
				"   data " +
				"FROM image I " +
				"JOIN message_image MI on I.image_id = MI.image_id " +
				"WHERE thread_id = ? AND message_id = ?");

		SAVE_MESSAGE_IMAGE = connection.prepareStatement("SELECT SaveImage(?, ?, ?)");
		//endregion

		if (api != null && api.isRunning()) {
			api.databaseReload(connection);
		}
	}

	public MessageThread getThread(String threadName) throws SQLException {
		checkConnection();
		GET_THREAD.setString(1, threadName);
		ResultSet resultSet = GET_THREAD.executeQuery();
		if (resultSet.next()) {
			return MessageThread.fromResultSet(config, resultSet);
		}
		return null;
	}

	public Human getHuman(String name) throws SQLException {
		checkConnection();
		GET_HUMAN.setString(1, name);
		ResultSet resultSet = GET_HUMAN.executeQuery();
		if (resultSet.next()) {
			return Human.fromResultSet(config, resultSet);
		}
		return null;
	}

	public Integer getHumanIdWithNameLike(String name) throws SQLException {
		checkConnection();
		GET_HUMAN_WITH_NAME_LIKE.setString(1, name);
		ResultSet resultSet = GET_HUMAN_WITH_NAME_LIKE.executeQuery();
		if (resultSet.next()) {
			return resultSet.getInt("human_id");
		}
		return null;
	}

	public Human getHumanWithNameLike(String name) throws SQLException {
		checkConnection();
		GET_HUMAN_WITH_NAME_LIKE.setString(1, name);
		ResultSet resultSet = GET_HUMAN_WITH_NAME_LIKE.executeQuery();
		if (resultSet.next()) {
			return Human.fromResultSet(config, resultSet);
		}
		return null;
	}

	//endregion

	private Message saveMessage(Message message, boolean noReturn) throws SQLException, IOException {
		checkConnection();
		try {
			SAVE_MESSAGE.setInt(1, message.getThread().getId());
			SAVE_MESSAGE.setString(2, message.getSender().getName());
			SAVE_MESSAGE.setTimestamp(3, Timestamp.valueOf(message.getDate().getDate()));

			ResultSet resultSet = SAVE_MESSAGE.executeQuery();
			if (resultSet.next()) {
				int textCount = 0;
				int imageCount = 0;
				int messageId = resultSet.getInt("message_id");
				for (MessageComponent component : message.getComponents()) {
					if (component instanceof Text) {
						saveText(messageId, (Text) component);
						textCount++;
					} else if (component instanceof Image) {
						saveImage(messageId, (Image) component);
						imageCount++;
					}
				}
				if (textCount > 0) {
					SAVE_MESSAGE_TEXT.executeBatch();
					SAVE_MESSAGE_TEXT.clearBatch();
				}
				if (imageCount > 0) {
					SAVE_MESSAGE_IMAGE.executeBatch();
					SAVE_MESSAGE_IMAGE.clearBatch();
				}

				if (!noReturn) {
					return Message.fromResultSet(config, this, resultSet);
				}
			}
		} catch (NullPointerException e) {
			if (api.debugging()) {
				System.out.println(message);
			}
		}
		return null;
	}

	public Message saveMessage(Message message) throws SQLException, IOException {
		return saveMessage(message, false);
	}

	public void saveMessageNoReturn(Message message) throws SQLException, IOException {
		saveMessage(message, true);
	}

	public Message getMessage(int messageId) throws SQLException {
		checkConnection();
		GET_MESSAGE.setInt(1, thread.getId());
		GET_MESSAGE.setInt(2, messageId);
		ResultSet resultSet = GET_MESSAGE.executeQuery();
		if (resultSet.next()) {
			return Message.fromResultSet(config, this, resultSet);
		}
		return null;
	}

	private void saveImage(int messageId, Image image) throws SQLException, IOException {
		SAVE_MESSAGE_IMAGE.setInt(1, thread.getId());
		SAVE_MESSAGE_IMAGE.setInt(2, messageId);
		InputStream stream = image.toStream();
		SAVE_MESSAGE_IMAGE.setBinaryStream(3, stream, stream.available());
		SAVE_MESSAGE_IMAGE.addBatch();
	}

	private void saveText(int messageId, Text text) throws SQLException {
		checkConnection();
		SAVE_MESSAGE_TEXT.setInt(1, thread.getId());
		SAVE_MESSAGE_TEXT.setInt(2, messageId);
		SAVE_MESSAGE_TEXT.setString(3, text.getText());
		SAVE_MESSAGE_TEXT.addBatch();
	}

	public List<MessageComponent> getMessageComponents(int messageId) throws SQLException {
		checkConnection();
		List<MessageComponent> components = new ArrayList<>();
		GET_MESSAGE_TEXT.setInt(1, thread.getId());
		GET_MESSAGE_TEXT.setInt(2, messageId);
		ResultSet resultSet = GET_MESSAGE_TEXT.executeQuery();
		while (resultSet.next()) {
			components.add(Text.fromResultSet(config, resultSet));
		}

		GET_MESSAGE_IMAGE.setInt(1, thread.getId());
		GET_MESSAGE_IMAGE.setInt(2, messageId);
		resultSet = GET_MESSAGE_IMAGE.executeQuery();
		while (resultSet.next()) {
			components.add(Image.fromResultSet(config, resultSet));
		}
		return components;
	}

	public Config getConfig() {
		return config;
	}

	public Connection getConnection() {
		checkConnection();
		return connection;
	}

	private Message getLatestMessage() throws SQLException {
		checkConnection();
		GET_LATEST_MESSAGE.setInt(1, thread.getId());
		ResultSet resultSet = GET_LATEST_MESSAGE.executeQuery();
		if (resultSet.next()) {
			return Message.fromResultSet(config, this, resultSet);
		}
		return null;
	}

	public MessageThread getThread() {
		return thread;
	}
}