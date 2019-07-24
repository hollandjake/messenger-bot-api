CREATE TABLE IF NOT EXISTS human
(
    human_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name     TEXT         NOT NULL,

    PRIMARY KEY (human_id)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE TABLE IF NOT EXISTS text
(
    text_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    text    TEXT         NOT NULL,

    PRIMARY KEY (text_id)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE TABLE IF NOT EXISTS image
(
    image_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    data     LONGBLOB     NOT NULL,

    PRIMARY KEY (image_id)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE TABLE IF NOT EXISTS thread
(
    thread_id   INT UNSIGNED NOT NULL AUTO_INCREMENT,
    thread_name TEXT         NOT NULL,

    PRIMARY KEY (thread_id)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE TABLE IF NOT EXISTS message
(
    message_id INT UNSIGNED NOT NULL,
    thread_id  INT UNSIGNED NOT NULL,
    sender_id  INT UNSIGNED NOT NULL,
    date       DATETIME     NOT NULL,

    PRIMARY KEY (message_id, thread_id),
    FOREIGN KEY (thread_id) REFERENCES thread (thread_id),
    FOREIGN KEY (sender_id) REFERENCES human (human_id)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE TABLE IF NOT EXISTS message_text
(
    thread_id  INT UNSIGNED NOT NULL,
    message_id INT UNSIGNED NOT NULL,
    text_id    INT UNSIGNED NOT NULL,

    PRIMARY KEY (message_id, thread_id, text_id),
    FOREIGN KEY (thread_id) REFERENCES message (thread_id),
    FOREIGN KEY (message_id) REFERENCES message (message_id),
    FOREIGN KEY (text_id) REFERENCES text (text_id)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

CREATE TABLE IF NOT EXISTS message_image
(
    thread_id  INT UNSIGNED NOT NULL,
    message_id INT UNSIGNED NOT NULL,
    image_id   INT UNSIGNED NOT NULL,

    PRIMARY KEY (message_id, thread_id, image_id),
    FOREIGN KEY (thread_id) REFERENCES message (thread_id),
    FOREIGN KEY (message_id) REFERENCES message (message_id),
    FOREIGN KEY (image_id) REFERENCES image (image_id)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_bin;

DROP FUNCTION IF EXISTS SaveHumanToId;
CREATE FUNCTION SaveHumanToId(hName TEXT)
    RETURNS INT
BEGIN
    DECLARE humanId INT DEFAULT (SELECT human_id FROM human WHERE name = hName);
    IF (ISNULL(humanId)) THEN
        INSERT INTO human (name) VALUES (hName);
        SET humanId = LAST_INSERT_ID();
    END IF;

    RETURN humanId;
END;

DROP PROCEDURE IF EXISTS GetHuman;
CREATE PROCEDURE GetHuman(IN hName TEXT)
BEGIN
    DECLARE humanId INT DEFAULT (SELECT human_id FROM human WHERE name = hName);
    IF (ISNULL(humanId)) THEN
        INSERT INTO human (name) VALUES (hName);
        SET humanId = LAST_INSERT_ID();
    END IF;

    SELECT H.human_id,
           name
    FROM human H
    WHERE human_id = humanId;
END;

DROP PROCEDURE IF EXISTS GetHumanWithNameLike;
CREATE PROCEDURE GetHumanWithNameLike(IN hName TEXT)
BEGIN
    -- first check if the string is an exact match;
    DECLARE humanId INT DEFAULT (SELECT human_id FROM human WHERE name COLLATE UTF8MB4_GENERAL_CI = hName LIMIT 1);
    IF (ISNULL(humanId)) THEN
        SET humanId =
                (SELECT human_id FROM human WHERE name COLLATE UTF8MB4_GENERAL_CI LIKE CONCAT(hName, '%') LIMIT 1);
    END IF;
    IF (ISNULL(humanId)) THEN
        SET humanId =
                (SELECT human_id FROM human WHERE name COLLATE UTF8MB4_GENERAL_CI LIKE CONCAT('%', hName, '%') LIMIT 1);
    END IF;

    IF (NOT ISNULL(humanId)) THEN
        SELECT human_id,
               name
        FROM human
        WHERE human_id = humanId
        LIMIT 1;
    ELSE
        SELECT NULL;
    END IF;
END;

DROP FUNCTION IF EXISTS SaveImage;
CREATE FUNCTION SaveImage(threadId INT, messageId INT, imageData LONGBLOB)
    RETURNS INT
BEGIN
    DECLARE imageId INT DEFAULT (SELECT image_id FROM image WHERE data = imageData LIMIT 1);
    IF (ISNULL(imageId)) THEN
        INSERT INTO image (data) VALUES (imageData);
        SET imageId = LAST_INSERT_ID();
    END IF;

    INSERT INTO message_image (thread_id, message_id, image_id) VALUES (threadId, messageId, imageId);

    RETURN imageId;
END;

DROP FUNCTION IF EXISTS SaveText;
CREATE FUNCTION SaveText(threadId INT, messageId INT, content TEXT)
    RETURNS INT
BEGIN
    DECLARE textId INT DEFAULT (SELECT text_id FROM text WHERE text = content LIMIT 1);
    IF (ISNULL(textId)) THEN
        INSERT INTO text (text) VALUES (content);
        SET textId = LAST_INSERT_ID();
    END IF;

    INSERT INTO message_text (thread_id, message_id, text_id) VALUES (threadId, messageId, textId);

    RETURN textId;
END;

DROP FUNCTION IF EXISTS GetLatestMessageId;
CREATE FUNCTION GetLatestMessageId(threadId INT)
    RETURNS INT
BEGIN
    DECLARE bestId INT DEFAULT (SELECT MAX(message_id) FROM message WHERE thread_id = threadId);
    IF (ISNULL(bestId)) THEN
        SET bestId = 0;
    END IF;

    RETURN bestId;
END;



DROP PROCEDURE IF EXISTS SaveMessage;
CREATE PROCEDURE SaveMessage(IN threadId INT, IN senderName TEXT, IN createdAt DATETIME)
BEGIN
    DECLARE newId INT DEFAULT GetLatestMessageId(threadId) + 1;

    -- Save Sender
    DECLARE senderId INT DEFAULT SaveHumanToId(senderName);

    INSERT INTO message (message_id, thread_id, sender_id, date) VALUES (newId, threadId, senderId, createdAt);

    SELECT M.message_id,
           date,
           H.human_id,
           name
    FROM message M
             JOIN human H on M.sender_id = H.human_id
    WHERE M.thread_id = threadId
      AND M.message_id = newId
    LIMIT 1;
END;

DROP PROCEDURE IF EXISTS GetThread;
CREATE PROCEDURE GetThread(IN threadName TEXT)
BEGIN
    -- Only Check against data since this is unique
    DECLARE threadId INT DEFAULT (SELECT thread_id FROM thread WHERE thread_name = threadName);

    -- If image already exists then don't both making it twice so just create a new link
    IF (ISNULL(threadId)) THEN
        INSERT INTO thread (thread_name) VALUES (threadName);
        SET threadId = LAST_INSERT_ID();
    END IF;

    SELECT T.thread_id,
           thread_name
    FROM thread T
    WHERE T.thread_id = threadId
    LIMIT 1;
END;

/*
 METHOD SCHEMAS
 */
DROP PROCEDURE IF EXISTS GetLatestMessage;
CREATE PROCEDURE GetLatestMessage(IN threadId INT)
BEGIN
    SELECT M.message_id,
           date,
           H.human_id,
           name
    FROM message M
             JOIN human H on M.sender_id = H.human_id
    WHERE M.thread_id = threadId
      AND M.message_id = GetLatestMessageId(threadId)
    LIMIT 1;
END;
